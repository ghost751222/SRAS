package com.consiliuminc.sras.authentications;


import com.consiliuminc.sras.entities.sqlserver.*;
import com.consiliuminc.sras.service.sqlserver.GroupFunctionService;
import com.consiliuminc.sras.service.sqlserver.MenuFunctionService;
import com.consiliuminc.sras.service.sqlserver.UserGroupService;
import com.consiliuminc.sras.service.sqlserver.UserService;
import com.consiliuminc.sras.vo.UserMenuVo;
import com.sun.jndi.toolkit.url.Uri;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class LoginAuthProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(LoginAuthProvider.class);


    @Value("${sras.ad.isad:false}")
    private boolean isAD;
    @Value("${sras.ad.domain:#{null}}")
    private String domain;
    @Value("${sras.ad.ldap:#{null}}")
    private String ldap;
    @Value("${sras.ad.base:#{null}}")
    private String adbase;

    private PasswordEncoder passwordEncoder;
    private UserService userService;
    private HttpSession httpSession;
    private UserGroupService userGroupService;
    private GroupFunctionService groupFunctionService;
    private MenuFunctionService menuFunctionService;


    @Autowired
    @Qualifier("sqlServerEntityManagerFactory")
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    public LoginAuthProvider(PasswordEncoder passwordEncoder, UserService userService, HttpSession httpSession, UserGroupService userGroupService, GroupFunctionService groupFunctionService, MenuFunctionService menuFunctionService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.httpSession = httpSession;
        this.userGroupService = userGroupService;
        this.groupFunctionService = groupFunctionService;
        this.menuFunctionService = menuFunctionService;
    }


    @Override
    public Authentication authenticate(Authentication auth)
            throws AuthenticationException {
        String account = auth.getName().toLowerCase();
        String password = auth.getCredentials().toString();




//        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
//
//        Session session = sessionFactory.openSession();
//        String sql = "select * from sras_user,sras_group,sras_function,sras_menu,sras_user_group,sras_group_function,sras_menu_function " +
//                "where sras_user.id = sras_user_group.userid " +
//                " and sras_group.id =sras_user_group.groupid " +
//                " and sras_group.id = sras_group_function.groupid " +
//                " and sras_function.program_name = sras_group_function.program_name " +
//                " and　sras_function.program_name = sras_menu_function.program_name " +
//                " and sras_menu.menu_name = sras_menu_function.menu_name";
//        SQLQuery query = session.createSQLQuery(sql);
//        query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
//        List results = query.list();
//        sessionFactory.close();
        boolean escapeAdCheck = false;
        if ("admin".equals(account)) escapeAdCheck = true;
        if (isAD && !escapeAdCheck) {
            boolean IsOK = this.AdCheck(account, password);
            if (!IsOK) {
                throw new BadCredentialsException("External system authentication failed");
            }
        }

        List<User> users = userService.findByAccount(account);
        if (isAD && !escapeAdCheck)
            users = users.stream().filter(user -> user.getAccount().equals(account)).collect(Collectors.toList());
        else
            users = users.stream().filter(user -> passwordEncoder.matches(password, user.getPassword())).collect(Collectors.toList());
        if (users.size() == 0) {
            //logger.info("account = {} , password ={}", account, password);
            throw new BadCredentialsException("External system authentication failed");
        } else {


            UserMenuVo vo = new UserMenuVo();
            User user = users.get(0);


            vo.setUserId(user.getId());
            List<UserGroup> userGroups = userGroupService.findByUserIdOrderBy(vo.getUserId());
            List<MenuFunction> menuFunctions = menuFunctionService.findAllOrderBySeq();
            menuFunctions.forEach(menuFunction -> {
                Function function = menuFunction.getFunction();
                Menu menu = menuFunction.getMenu();
                String menuDisplayName = menu.getDisplay_name();
                userGroups.forEach(userGroup -> {
                    List<GroupFunction> groupFunctions = groupFunctionService.findByGroupId(userGroup.getGroup().getId());
                    groupFunctions.forEach(groupFunction -> {
                        String program_name = groupFunction.getFunction().getProgram_name();
                        if (program_name.equals(function.getProgram_name())) {
                            Map<Menu, List<Function>> menuListMap = vo.getMenuMap();
                            if (menuListMap.get(menu) == null) {
                                menuListMap.put(menu, new ArrayList<>());
                                menuListMap.get(menu).add(function);
                            } else {
                                boolean isExist = menuListMap.get(menu).stream().anyMatch(f -> f.getProgram_name().equals(program_name));
                                if (!isExist)
                                    menuListMap.get(menu).add(function);
                            }


                        }
                    });


                });
            });

            Map<Menu, List<Function>> newMapSortedByKey = vo.getMenuMap().entrySet().stream()
                    .sorted(Comparator.comparing(e -> e.getKey().getSeq()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            httpSession.setAttribute("user", user);
            httpSession.setAttribute("usermenu", newMapSortedByKey);
            return new UsernamePasswordAuthenticationToken
                    (account, password, Collections.emptyList());
        }
    }

    private boolean AdCheck(String account, String password) {

        try {
            if (StringUtils.isEmpty(domain) || StringUtils.isEmpty(ldap) || StringUtils.isEmpty(adbase)) {
                throw new Exception("AD Information is not Setting ");
            }
            Properties env = new Properties();
            // 使用UPN格式：User@domain或SamAccountName格式：domain\\User
            String adminName = account + "@" + domain;
            String adminPassword = password;

            URI uri =  URI.create(ldap);
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.SECURITY_AUTHENTICATION, "simple");// LDAP訪問安全級別："none","simple","strong"
            env.put(Context.SECURITY_PRINCIPAL, adminName);// AD User
            env.put(Context.SECURITY_CREDENTIALS, adminPassword);// AD Password
            env.put(Context.PROVIDER_URL, ldap);// LDAP工廠類

            if(  uri.getScheme().equalsIgnoreCase("ldaps")){
                env.put(Context.SECURITY_PROTOCOL, "ssl");
                env.put("java.naming.ldap.factory.socket", CustomSSLSocketFactory.class.getName());
            }

            InitialDirContext ctx = new InitialDirContext(env);


            // 搜索控制器
//            SearchControls searchCtls = new SearchControls();
//            // 創建搜索控制器
//            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
//            // LDAP搜索過濾器類，此處只獲取AD域用户，所以條件為用户user或者person均可
//            // (&(objectCategory=person)(objectClass=user)(name=*))
//            String searchFilter = String.format("sAMAccountName=%s", account);
//            // AD域節點結構
//            String searchBase = adbase;
//            String returnedAtts[] = {"sn", "cn", "mail", "name", "userPrincipalName",
//                    "department", "sAMAccountName", "whenChanged"};
//            searchCtls.setReturningAttributes(returnedAtts);
//            NamingEnumeration<SearchResult> answer = ctx.search(searchBase, searchFilter, searchCtls);


            ctx.close();
            return true;
        } catch (Exception e) {
            logger.error("AD Login Error ={}", e);
            return false;
        }


    }

    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}
