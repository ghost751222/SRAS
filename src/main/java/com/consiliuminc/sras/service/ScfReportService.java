package com.consiliuminc.sras.service;

import com.consiliuminc.sras.configs.PsaeConfig;
import com.consiliuminc.sras.consts.AppConst;
import com.consiliuminc.sras.entities.postgres.QaInterfaceDim;
import com.consiliuminc.sras.entities.postgres.QaInterfaceRoleDim;
import com.consiliuminc.sras.entities.postgres.QaTaskJob;
import com.consiliuminc.sras.service.postgres.QaInterfaceDimService;
import com.consiliuminc.sras.service.postgres.QaInterfaceRoleDimService;
import com.consiliuminc.sras.service.postgres.QaTaskJobService;
import com.consiliuminc.sras.util.DateUtil;
import com.consiliuminc.sras.util.JacksonUtils;
import com.consiliuminc.sras.vo.ScfCustomHeaderVo;
import com.consiliuminc.sras.vo.StandardModelGroupVo;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFPivotCacheDefinition;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;

@Service
public class ScfReportService {

    private static final Logger logger = LoggerFactory.getLogger(ScfReportService.class);

    private QaTaskJobService qaTaskJobService;

    private QaInterfaceDimService qaInterfaceDimService;

    private QaModelService qaModelService;

    private QaInterfaceRoleDimService qaInterfaceRoleDimService;

    private PsaeConfig psaeConfig;

    private ThreadPoolTaskExecutor task;

    private TaskExecutor taskReportExecutor;


    @Autowired
    public ScfReportService(QaTaskJobService qaTaskJobService, QaInterfaceDimService qaInterfaceDimService, QaModelService qaModelService, QaInterfaceRoleDimService qaInterfaceRoleDimService, PsaeConfig psaeConfig, @Qualifier("taskReportExecutor") TaskExecutor taskReportExecutor) {
        this.qaTaskJobService = qaTaskJobService;
        this.qaInterfaceDimService = qaInterfaceDimService;
        this.qaModelService = qaModelService;
        this.qaInterfaceRoleDimService = qaInterfaceRoleDimService;
        this.psaeConfig = psaeConfig;
        this.taskReportExecutor = taskReportExecutor;
        task = ((ThreadPoolTaskExecutor) taskReportExecutor);
    }


    private InputStream loadReportStream() throws IOException {
        ClassPathResource resource = new ClassPathResource("report/report.xlsx");
        InputStream inputStream = resource.getInputStream();
        return inputStream;
    }

    public ByteArrayOutputStream exportReport(String month, String sales_sdate, String sales_edate) throws Exception {
        LocalDate start = DateUtil.toLocalDate(sales_sdate);
        LocalDate end = DateUtil.toLocalDate(sales_edate);
        return this.exportReport(month, start, end);
    }


    // @Scheduled(fixedDelay = 10000)
    public void showTaskExecutorInfo() {
        logger.info("taskReportExecutor 當前活動線程數={} ,taskReportExecutor 線程處理隊列長度 ={}", task.getActiveCount(), task.getThreadPoolExecutor().getQueue().size());
    }

    private ByteArrayOutputStream exportReport(String month, LocalDate start, LocalDate end) throws Exception {


        logger.info("exportReport begin LocalDate start= {} end ={} ", start, end);
        String businessMonth = month;
        Workbook workbook = new XSSFWorkbook(loadReportStream());
        List<QaInterfaceDim> qaInterfaceDims = this.getQaInterfaceDims();
        List<StandardModelGroupVo> standardModelGroupVos = getStandardModelList(workbook.getSheetAt(9));
        List<String> scfCustomHeaderVoList = getCustomizedHeaderList();
        List<QaTaskJob> qaTaskJobs = qaTaskJobService.findByTaskTLSubmitTime(start, end);
        List<List<StandardModelGroupVo>> cmDateReportDetailGroupModel = new ArrayList<>();
        Integer excelIndex = 1;

        //CM report parameter
        Map<String, Integer> cmReportMap = new HashMap<>();
        cmReportMap.put(AppConst.AUTO_CRITICAL_QUANTITY, 0);
        cmReportMap.put(AppConst.AUTO_CONFIRM_QUANTITY, 0);
        cmReportMap.put(AppConst.AUTO_QUALIFIED_QUANTITY, 0);
        cmReportMap.put(AppConst.RESALES_QUANTITY, 0);
        cmReportMap.put(AppConst.LACKOFOPEN_QUANTITY, 0);
        cmReportMap.put(AppConst.OPENASASSET_QUANTITY, 0);
        cmReportMap.put(AppConst.UNKNOW_MERCHANDISE_QUANTITY, 0);

        setCMDateReportHeader(workbook.getSheetAt(5), standardModelGroupVos, qaInterfaceDims, scfCustomHeaderVoList);
        for (QaTaskJob qaTaskJob : qaTaskJobs) {
            Integer finalExcelIndex = excelIndex;

            List<StandardModelGroupVo> vos = getDefectDetailData(qaTaskJob, standardModelGroupVos);
            cmDateReportDetailGroupModel.add(vos);

            List<ScfCustomHeaderVo> scfCustomHeaderVos = getScfCustomizeData(businessMonth, qaTaskJob, vos, scfCustomHeaderVoList, cmReportMap);
            setCMDateReportData(finalExcelIndex, workbook.getSheetAt(5), qaTaskJob, vos, qaInterfaceDims, scfCustomHeaderVos);
            excelIndex++;

        }

        setRCReportData(businessMonth, workbook.getSheetAt(0));
        setCMReportData(workbook.getSheetAt(1), businessMonth, cmReportMap);
        setSCFMonthlyReportData(workbook.getSheetAt(3));
        setDefectStatisticsData(workbook.getSheetAt(6), cmDateReportDetailGroupModel);
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
        workbook.write(bOutput);
        logger.info("exportReport finish LocalDate start= {} end ={} ", start, end);
        return bOutput;

    }


    private void setRCReportData(String businessMonth, Sheet sheet) {
        sheet.getRow(4).getCell(1).setCellValue(businessMonth);
        sheet.getRow(16).getCell(1).setCellValue(businessMonth);
        reloadPivotTable(sheet);
    }


    private void setCMReportData(Sheet sheet, String businessMonth, Map<String, Integer> cmReportMap) {
        Integer autoCriticalQuantity = cmReportMap.get(AppConst.AUTO_CRITICAL_QUANTITY);
        Integer autoQualifiedQuantity = cmReportMap.get(AppConst.AUTO_QUALIFIED_QUANTITY);
        Integer autoConfirmQuantity = cmReportMap.get(AppConst.AUTO_CONFIRM_QUANTITY);
        Integer reSalesQuantity = cmReportMap.get(AppConst.RESALES_QUANTITY);
        Integer lackOfOpenQuantity = cmReportMap.get(AppConst.LACKOFOPEN_QUANTITY);
        Integer openAsAssetQuantity = cmReportMap.get(AppConst.OPENASASSET_QUANTITY);
        Integer ensureUnclearQuantity = cmReportMap.get(AppConst.UNKNOW_MERCHANDISE_QUANTITY);


        Integer autoTotal = autoCriticalQuantity + autoQualifiedQuantity + autoConfirmQuantity;
        Integer total = lackOfOpenQuantity + openAsAssetQuantity + ensureUnclearQuantity;

        sheet.getRow(8).getCell(1).setCellValue(businessMonth);

        sheet.getRow(11).getCell(1).setCellValue(autoConfirmQuantity); //其他待覆核
        sheet.getRow(12).getCell(1).setCellValue(autoQualifiedQuantity); //合格
        sheet.getRow(13).getCell(1).setCellValue(autoCriticalQuantity); //重大瑕疵(開場白缺漏
        sheet.getRow(14).getCell(1).setCellValue(autoTotal);

        sheet.getRow(22).getCell(1).setCellValue(lackOfOpenQuantity); //開場白缺漏
        sheet.getRow(23).getCell(1).setCellValue(openAsAssetQuantity); //以累積財富或利率為開場
        sheet.getRow(24).getCell(1).setCellValue(ensureUnclearQuantity); //保障/權益說明不清
        sheet.getRow(25).getCell(1).setCellValue(total);

        sheet.getRow(27).getCell(1).setCellValue(reSalesQuantity); //重大瑕疵退件重新銷售件數
        reloadPivotTable(sheet);
    }


    private void setSCFMonthlyReportData(Sheet sheet) {
        reloadPivotTable(sheet);
    }

    private void setDefectStatisticsData(Sheet sheet, List<List<StandardModelGroupVo>> cmDateReportDetailGroupModel) {
        int index = 0;
        StandardModelGroupVo vo = null;
        Map<String, Integer> sumList = new HashMap<>();
        for (List<StandardModelGroupVo> vos : cmDateReportDetailGroupModel) {
            for (StandardModelGroupVo v : vos) {
                Integer sum = sumList.get(v.getGroupName());
                if (sum == null) {
                    sumList.put(v.getGroupName(), 0);
                } else {
                    sumList.put(v.getGroupName(), sum + v.getCount());
                }
            }

        }


        String cellValue;
        int lastRowNum = sheet.getLastRowNum();
        for (index = 0; index < lastRowNum; index++) {
            cellValue = sheet.getRow(index).getCell(0).getStringCellValue();
            for (Map.Entry<String, Integer> entry : sumList.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(cellValue)) {
                    sheet.getRow(index).getCell(1).setCellValue(entry.getValue());
                }
            }
        }

    }


    private List<ScfCustomHeaderVo> getScfCustomizeData(String businessMonth, QaTaskJob qaTaskJob, List<StandardModelGroupVo> standardModelGroupVos, List<String> scfCustomHeaderVoList, Map<String, Integer> cmReportMap) throws IOException {
        List<ScfCustomHeaderVo> scfCustomHeaderVos = new ArrayList<>();
        int cmRejectQuantity = 0, cmCriticalQuantity = 0;
        //CM report parameter
        Integer autoCriticalQuantity = 0, autoQualifiedQuantity = 0, autoConfirmQuantity = 0;
        Integer reSalesQuantity = 0;
        Integer lackOfOpenQuantity = 0, openAsAssetQuantity = 0, unKnowMerchandiseQuantity = 0;
        JsonNode jsonNode = JacksonUtils.toJsonNode(qaTaskJob.getTask());
        String qa_case_content = qaTaskJob.getCase_content();

        for (String header : scfCustomHeaderVoList) {
            ScfCustomHeaderVo vo = new ScfCustomHeaderVo();
            vo.setHeaderName(header);
            scfCustomHeaderVos.add(vo);
            switch (header) {
                case "業績月":
                    vo.setData(Integer.parseInt(businessMonth));
                    break;
                case "當月總數量":
                case "RC數量":
                    vo.setData(1);
                    break;
                case "簡訊數量":
                    vo.setData(getMessageSendEvent(jsonNode));
                    break;
                case "RC處理方式":
                    if (getMessageSendEvent(jsonNode) == 1) {
                        vo.setData("簡訊發送");
                    } else {
                        vo.setData("RC");
                    }
                    break;
                case "純RC退件數":
                    vo.setData(getRcRejectData(jsonNode));
                    break;
                case "大覆核數量":
                    vo.setData(getMajorConfirmData(jsonNode));
                    break;
                case "小覆核數量":
                    vo.setData(getMinorConfirmData(jsonNode));
                    break;
                case "事前CM抽件數量":
                    vo.setData(getCMDrawData(jsonNode));
                    break;
                case "事前CM退件數量":
                    //各瑕疵統計
                    Integer total = 0;
                    for (StandardModelGroupVo standardModelGroupVo : standardModelGroupVos) {
                        total += standardModelGroupVo.getCount();
                    }
                    if (total > 0 && getCMDrawData(jsonNode) == 1) {
                        cmRejectQuantity = 1;
                    }
                    vo.setData(cmRejectQuantity);
                    break;
                case "事前CM重大瑕疵數量":
                    cmCriticalQuantity = getCMCriticalData(jsonNode, standardModelGroupVos);
                    vo.setData(cmCriticalQuantity);
                    break;
                case "事前CM未補全數量":
                    vo.setData(getCMNotCompleteData(jsonNode));
                    break;
                case "事前CM保全失敗+逾期數量":
                    vo.setData(getCMDelayData(jsonNode));
                    break;
                case "事前CM未補全或取消件數量":
                    vo.setData(getCMNotCompleteData(jsonNode) + getCMDelayData(jsonNode));
                    break;
                case "事前CM其他瑕疵數量(RC+CM退件-重大瑕疵)":
                    vo.setData(getRcRejectData(jsonNode) + cmRejectQuantity - cmCriticalQuantity);
                    break;
                case "自動質檢結果：其他待覆核數量":
                    if ("待覆核".equalsIgnoreCase(qa_case_content)) {
                        autoConfirmQuantity = 1;
                    } else
                        autoConfirmQuantity = 0;

                    cmReportMap.put(AppConst.AUTO_CONFIRM_QUANTITY, cmReportMap.get(AppConst.AUTO_CONFIRM_QUANTITY) + autoConfirmQuantity);
                    vo.setData(autoConfirmQuantity);
                    break;
                case "自動質檢結果：合格數量":
                    if ("合格".equalsIgnoreCase(qa_case_content)) {
                        autoQualifiedQuantity = 1;
                    } else
                        autoQualifiedQuantity = 0;
                    cmReportMap.put(AppConst.AUTO_QUALIFIED_QUANTITY, cmReportMap.get(AppConst.AUTO_QUALIFIED_QUANTITY) + autoQualifiedQuantity);
                    vo.setData(autoQualifiedQuantity);
                    break;
                case "自動質檢結果：退件重新銷售數量":
                    if ("重大瑕疵".equalsIgnoreCase(qa_case_content)) {
                        autoCriticalQuantity = 1;
                    } else
                        autoCriticalQuantity = 0;
                    cmReportMap.put(AppConst.AUTO_CRITICAL_QUANTITY, cmReportMap.get(AppConst.AUTO_CRITICAL_QUANTITY) + autoCriticalQuantity);
                    vo.setData(autoCriticalQuantity);
                    break;

                case "重大瑕疵原因：開場白缺漏件數":
                    lackOfOpenQuantity = getCriticalLackOfBeginningData(jsonNode, standardModelGroupVos);
                    cmReportMap.put(AppConst.LACKOFOPEN_QUANTITY, cmReportMap.get(AppConst.LACKOFOPEN_QUANTITY) + lackOfOpenQuantity);
                    vo.setData(lackOfOpenQuantity);
                    break;

                case "重大瑕疵原因：以累積財富或利率為開場件數":
                    openAsAssetQuantity = getCriticalOpenAsAssetData(jsonNode, standardModelGroupVos);
                    cmReportMap.put(AppConst.OPENASASSET_QUANTITY, cmReportMap.get(AppConst.OPENASASSET_QUANTITY) + lackOfOpenQuantity);
                    vo.setData(openAsAssetQuantity);
                    break;

                case "重大瑕疵原因：客戶不了解商品內容件數":
                    unKnowMerchandiseQuantity = getCriticalUnKnowMerchandiseData(jsonNode, standardModelGroupVos);
                    cmReportMap.put(AppConst.UNKNOW_MERCHANDISE_QUANTITY, cmReportMap.get(AppConst.UNKNOW_MERCHANDISE_QUANTITY) + unKnowMerchandiseQuantity);
                    vo.setData(unKnowMerchandiseQuantity);

                    break;
                case "重大瑕疵退件重新銷售件數":
                    reSalesQuantity = getReSalesData(jsonNode);
                    cmReportMap.put(AppConst.RESALES_QUANTITY, cmReportMap.get(AppConst.RESALES_QUANTITY) + unKnowMerchandiseQuantity);
                    vo.setData(reSalesQuantity);
                    break;

            }
        }

        return scfCustomHeaderVos;
    }

    private List<String> getCustomizedHeaderList() {


        List<String> customizedHeader = new ArrayList<>();


        customizedHeader.add("業績月");
        customizedHeader.add("當月總數量");
        customizedHeader.add("RC數量");
        customizedHeader.add("RC處理方式");
        customizedHeader.add("簡訊數量");
        customizedHeader.add("純RC退件數");
        customizedHeader.add("大覆核數量");
        customizedHeader.add("小覆核數量");
        customizedHeader.add("事前CM抽件數量");
        customizedHeader.add("事前CM退件數量");
        customizedHeader.add("事前CM重大瑕疵數量");
        customizedHeader.add("事前CM未補全數量");
        customizedHeader.add("事前CM保全失敗+逾期數量");
        customizedHeader.add("事前CM未補全或取消件數量");
        customizedHeader.add("事前CM其他瑕疵數量(RC+CM退件-重大瑕疵)");
        customizedHeader.add("事後CM抽件數量");
        customizedHeader.add("事後CM瑕疵件數");
        customizedHeader.add("事後CM未補全數量");
        customizedHeader.add("自動質檢結果：其他待覆核數量");
        customizedHeader.add("自動質檢結果：合格數量");
        customizedHeader.add("自動質檢結果：退件重新銷售數量");
        customizedHeader.add("重大瑕疵原因：開場白缺漏件數");
        customizedHeader.add("重大瑕疵原因：以累積財富或利率為開場件數");
        customizedHeader.add("重大瑕疵原因：客戶不了解商品內容件數");
        customizedHeader.add("重大瑕疵退件重新銷售件數");

        return customizedHeader;
    }

    private void setCMDateReportHeader(Sheet sheet, List<StandardModelGroupVo> standardModelGroupVos, List<QaInterfaceDim> qaInterfaceDims, List<String> scfCustomHeaderVoList) {
        Integer excelIndex = 0;
        Row row = sheet.getRow(0);
        Cell cell;
        for (int i = 0; i < qaInterfaceDims.size(); i++) {
            cell = row.createCell(excelIndex++);
            cell.setCellValue(qaInterfaceDims.get(i).getDisplay_name());
        }

        for (int j = 0; j < standardModelGroupVos.size(); j++) {
            cell = row.createCell(excelIndex++);
            cell.setCellValue(standardModelGroupVos.get(j).getGroupName());

        }

        cell = row.createCell(excelIndex++);
        cell.setCellValue(AppConst.DEFECT_STRING);

        for (int k = 0; k < scfCustomHeaderVoList.size(); k++) {
            cell = row.createCell(excelIndex++);
            cell.setCellValue(scfCustomHeaderVoList.get(k));
        }

    }

    //產生CM DATE 報表
    private void setCMDateReportData(Integer excelIndex, Sheet sheet, QaTaskJob qaTaskJob, List<StandardModelGroupVo> vos, List<QaInterfaceDim> qaInterfaceDims, List<ScfCustomHeaderVo> scfCustomHeaderVos) throws IOException, NoSuchFieldException, IllegalAccessException {

        String fieldName, fieldValue, type;
        Integer i, total = 0;
        Class<?> cls = qaTaskJob.getClass();
        Row row = sheet.createRow(excelIndex);
        Cell cell;
        JsonNode jsonNode = JacksonUtils.toJsonNode(qaTaskJob.getTask());
        //隨錄資訊
        for (i = 0; i < qaInterfaceDims.size(); i++) {
            fieldName = qaInterfaceDims.get(i).getField_name();
            type = qaInterfaceDims.get(i).getType();
            if ("task".equalsIgnoreCase(type)) {
                fieldValue = getJsonNodeText(jsonNode, fieldName);
            } else {
                Field field = cls.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(qaTaskJob);
                fieldValue = value == null ? Strings.EMPTY : value.toString();
            }
            cell = row.createCell(i);
            cell.setCellValue(fieldValue);
        }

        //各瑕疵統計
        for (StandardModelGroupVo standardModelGroupVo : vos) {
            total += standardModelGroupVo.getCount();
            cell = row.createCell(i++);
            cell.setCellValue(standardModelGroupVo.getCount());

        }
        cell = row.createCell(i++);
        cell.setCellValue(total);


        for (ScfCustomHeaderVo vo : scfCustomHeaderVos) {
            cell = row.createCell(i++);
            if (vo.getData() instanceof Integer) {
                cell.setCellValue(Integer.parseInt(vo.getData().toString()));
            } else {
                cell.setCellValue(vo.getData().toString());
            }

        }

    }

    //簡訊數量
    private Integer getMessageSendEvent(JsonNode jsonNode) {
        String tmrPendingCode1Name = getJsonNodeText(jsonNode, "psae_TMRPendingCode1Name");//jsonNode.get("psae_TMRPendingCode1Name").asText(); //pendingCode大項名稱
        if ("簡訊發送".equals(tmrPendingCode1Name))
            return 1;
        else
            return 0;
    }


    //CM Date Report 違規細項統計
    private List<StandardModelGroupVo> getDefectDetailData(QaTaskJob qaTaskJob, List<StandardModelGroupVo> standardModelGroupVos) throws IOException {
        boolean IsCalDefectData = false, IsExistNegativeModels = false, IsExistPositiveModels = false;
        StandardModelGroupVo detailVo;
        List<String> qaHitModels = null, qaNotHitModels = null;
        JsonNode jsonNode = JacksonUtils.toJsonNode(qaTaskJob.getTask());

        List<StandardModelGroupVo> detailModelGroupVo = new ArrayList<>();

        Integer jobId = qaTaskJob.getId();
        String qa_result = getJsonNodeText(jsonNode, "qa_result");//質檢結果
        if (qa_result.equalsIgnoreCase("不合格") || qa_result.equalsIgnoreCase("優秀")) {
            IsCalDefectData = true;
            Map<Boolean, List<String>> map = qaModelService.getAllQaGroupModel(jobId);
            qaHitModels = map.get(true);
            qaNotHitModels = map.get(false);
            qaHitModels = removePositiveModel(qaHitModels);
            qaNotHitModels = removeNegativeModel(qaNotHitModels);
            IsExistNegativeModels = negativeModelCheck(qaHitModels);
            IsExistPositiveModels = positiveModelCheck(qaNotHitModels);
        }


        for (StandardModelGroupVo vo : standardModelGroupVos) {
            detailVo = new StandardModelGroupVo(0, vo.getGroupName(), vo.getGroupModels());
            detailModelGroupVo.add(detailVo);
            List<String> groupModels = vo.getGroupModels();

            if (IsCalDefectData) {
                boolean IsExist;
                if (IsExistNegativeModels) {
                    for (String qaModel : qaHitModels) {
                        IsExist = groupModels.stream().anyMatch(g -> g.equalsIgnoreCase(qaModel));
                        if (IsExist) {
                            detailVo.setCount(detailVo.getCount() + 1);
                        }
                    }
                }

                if (!IsExistPositiveModels) {
                    for (String qaModel : qaNotHitModels) {
                        IsExist = groupModels.stream().anyMatch(g -> g.equalsIgnoreCase(qaModel));
                        if (IsExist) {
                            detailVo.setCount(detailVo.getCount() + 1);
                        }
                    }
                }
            }
        }
        return detailModelGroupVo;
    }

    //純RC退件數
    private Integer getRcRejectData(JsonNode jsonNode) {
        List<String> condition = Arrays.asList("TL要求退件,客戶反悔投保,客戶需求,資料有誤".split(","));
        String tmrPendingCode1Name = getJsonNodeText(jsonNode, "psae_TMRPendingCode1Name");
        String qa_result = getJsonNodeText(jsonNode, "qa_result");
        if ("合格".equalsIgnoreCase(qa_result) && condition.stream().anyMatch(c -> c.equalsIgnoreCase(tmrPendingCode1Name))) {
            return 1;
        } else {
            return 0;
        }


    }


    //大覆核數量
    private Integer getMajorConfirmData(JsonNode jsonNode) {
        String qa_result = getJsonNodeText(jsonNode, "qa_result");
        if ("不合格".equalsIgnoreCase(qa_result)) {
            return 1;
        } else {
            return 0;
        }


    }

    //小覆核數量
    private Integer getMinorConfirmData(JsonNode jsonNode) {
        String qa_result = getJsonNodeText(jsonNode, "qa_result");
        if ("優秀".equalsIgnoreCase(qa_result)) {
            return 1;
        } else {
            return 0;
        }


    }

    //事前CM抽件數量
    private Integer getCMDrawData(JsonNode jsonNode) {
        String qa_result = getJsonNodeText(jsonNode, "qa_result");
        if ("不合格".equalsIgnoreCase(qa_result)) {
            return 1;
        } else {
            return 0;
        }


    }


    //事前CM重大瑕疵數量
    private Integer getCMCriticalData(JsonNode jsonNode, List<StandardModelGroupVo> standardModelGroupVos) {

        String psae_TMRPendingCode2Name = getJsonNodeText(jsonNode, "psae_TMRPendingCode2Name");
        String psae_SCFRejectRemedyPendingCode2Name = getJsonNodeText(jsonNode, "psae_SCFRejectRemedyPendingCode2Name");
        boolean isHit = false;

        for (StandardModelGroupVo vo : standardModelGroupVos) {
            switch (vo.getGroupName()) {
                case "2_3_5":
                case "2_11_1":
                case "2_15_1":
                    if (vo.getCount() > 0)
                        isHit = true;
                    break;
            }
            if (isHit) break;
        }


        if (isHit && AppConst.CRITICAL_RESALE.equalsIgnoreCase(psae_TMRPendingCode2Name) && (!AppConst.APPLY_SUCCESS.equalsIgnoreCase(psae_SCFRejectRemedyPendingCode2Name) && !AppConst.TMR_CONFIRM.equalsIgnoreCase(psae_SCFRejectRemedyPendingCode2Name))) {
            return 1;
        } else
            return 0;
    }

    //事前CM未補全數量
    private Integer getCMNotCompleteData(JsonNode jsonNode) {
        String psae_SCFRejectRemedyPendingCode1Name = getJsonNodeText(jsonNode, "psae_SCFRejectRemedyPendingCode1Name");
        if (Strings.isEmpty(psae_SCFRejectRemedyPendingCode1Name)) {
            return 1;
        } else
            return 0;
    }

    //事前CM保全失敗+逾期數量
    private Integer getCMDelayData(JsonNode jsonNode) {
        String psae_SCFRejectRemedyPendingCode2Name = getJsonNodeText(jsonNode, "psae_SCFRejectRemedyPendingCode2Name");
        if ("『TMR無法聯絡上保戶補全照會，取消保單』".equalsIgnoreCase(psae_SCFRejectRemedyPendingCode2Name) || "『TMR保全失敗』".equalsIgnoreCase(psae_SCFRejectRemedyPendingCode2Name)) {
            return 1;
        } else
            return 0;
    }

    //重大瑕疵原因：開場白缺漏件數
    private Integer getCriticalLackOfBeginningData(JsonNode jsonNode, List<StandardModelGroupVo> standardModelGroupVos) {
        String psae_TMRPendingCode2Name = getJsonNodeText(jsonNode, "psae_TMRPendingCode2Name");
        String psae_SCFRejectRemedyPendingCode2Name = getJsonNodeText(jsonNode, "psae_SCFRejectRemedyPendingCode2Name");
        boolean isHit = false;

        for (StandardModelGroupVo vo : standardModelGroupVos) {
            if (vo.getGroupName().equalsIgnoreCase("2_11_1") && vo.getCount() > 0) {
                isHit = true;
            }
        }

        if (isHit && AppConst.CRITICAL_RESALE.equalsIgnoreCase(psae_TMRPendingCode2Name) && !AppConst.APPLY_SUCCESS.equalsIgnoreCase(psae_SCFRejectRemedyPendingCode2Name) && !AppConst.TMR_CONFIRM.equalsIgnoreCase(psae_SCFRejectRemedyPendingCode2Name)) {
            return 1;
        } else
            return 0;


    }

    // 重大瑕疵原因：以累積財富或利率為開場件數
    private Integer getCriticalOpenAsAssetData(JsonNode jsonNode, List<StandardModelGroupVo> standardModelGroupVos) {
        String psae_TMRPendingCode2Name = getJsonNodeText(jsonNode, "psae_TMRPendingCode2Name");
        String psae_SCFRejectRemedyPendingCode2Name = getJsonNodeText(jsonNode, "psae_SCFRejectRemedyPendingCode2Name");
        boolean isHit = false;

        for (StandardModelGroupVo vo : standardModelGroupVos) {
            if (vo.getGroupName().equalsIgnoreCase("2_3_5") && vo.getCount() > 0) {
                isHit = true;
            }
        }
        if (isHit && AppConst.CRITICAL_RESALE.equalsIgnoreCase(psae_TMRPendingCode2Name) && !AppConst.APPLY_SUCCESS.equalsIgnoreCase(psae_SCFRejectRemedyPendingCode2Name) && !AppConst.TMR_CONFIRM.equalsIgnoreCase(psae_SCFRejectRemedyPendingCode2Name)) {
            return 1;
        } else
            return 0;


    }

    //重大瑕疵原因：客戶不了解商品內容件數
    private Integer getCriticalUnKnowMerchandiseData(JsonNode jsonNode, List<StandardModelGroupVo> standardModelGroupVos) {
        String psae_TMRPendingCode2Name = getJsonNodeText(jsonNode, "psae_TMRPendingCode2Name");
        String psae_SCFRejectRemedyPendingCode2Name = getJsonNodeText(jsonNode, "psae_SCFRejectRemedyPendingCode2Name");
        boolean isHit = false;

        for (StandardModelGroupVo vo : standardModelGroupVos) {
            if (vo.getGroupName().equalsIgnoreCase("2_15_1") && vo.getCount() > 0) {
                isHit = true;
            }
        }
        if (isHit && AppConst.CRITICAL_RESALE.equalsIgnoreCase(psae_TMRPendingCode2Name) && !AppConst.APPLY_SUCCESS.equalsIgnoreCase(psae_SCFRejectRemedyPendingCode2Name) && !AppConst.TMR_CONFIRM.equalsIgnoreCase(psae_SCFRejectRemedyPendingCode2Name)) {
            return 1;
        } else
            return 0;

    }

    //重大瑕疵退件重新銷售件數
    private Integer getReSalesData(JsonNode jsonNode) {

        String resales = getJsonNodeText(jsonNode, "psae_ReSales");
        if ("Y".equals(resales))
            return 1;
        else
            return 0;
    }

    private void reloadPivotTable(Sheet sheet) {
        for (XSSFPivotTable pivotTable : ((XSSFSheet) sheet).getPivotTables()) {
            for (POIXMLDocumentPart documentPart : pivotTable.getRelations()) {
                if (documentPart instanceof XSSFPivotCacheDefinition) {
                    XSSFPivotCacheDefinition pivotCacheDefinition = (XSSFPivotCacheDefinition) documentPart;
                    pivotCacheDefinition.getCTPivotCacheDefinition().setRefreshOnLoad(true);

                }
            }
        }
    }

    private List<StandardModelGroupVo> getStandardModelList(Sheet sheet) {

        List<StandardModelGroupVo> data = new ArrayList<>();
        String groupName = Strings.EMPTY;
        List<String> groupModels;
        StandardModelGroupVo vo;
        for (Row row : sheet) {
            if (row.getRowNum() > 1) {
                try {
                    groupModels = new ArrayList<>(Arrays.asList(row.getCell(4).toString().replace(" ",Strings.EMPTY).split("\n")));
                } catch (Exception e) {
                    groupModels = new ArrayList<>();
                    logger.error("getStandardModelList groupModels error ={}", e.getMessage());
                }
                if (!Strings.isEmpty(row.getCell(0).toString())) {
                    groupName = row.getCell(0).toString();
                    vo = new StandardModelGroupVo(0, groupName, groupModels);
                    data.add(vo);
                } else {
                    String _groupName = groupName;
                    List<String> _groupModels = groupModels;
                    data.stream().filter(d -> d.getGroupName().equalsIgnoreCase(_groupName)).forEach(d -> {
                        d.getGroupModels().addAll(_groupModels);
                    });

                }
            }

        }
        return data;
    }

    private List<QaInterfaceDim> getQaInterfaceDims() {
        List<QaInterfaceDim> qaInterfaceDims = qaInterfaceDimService.findAllOrderByOrder();
        List<QaInterfaceRoleDim> qaInterfaceRoleDims = qaInterfaceRoleDimService.findAllByRoleId(4);

        Iterator<QaInterfaceDim> i = qaInterfaceDims.iterator();
        while (i.hasNext()) {
            QaInterfaceDim qaInterfaceDim = i.next(); // must be called before you can call i.remove()
            QaInterfaceRoleDim vo = qaInterfaceRoleDims.stream().filter(qaInterfaceRoleDim -> qaInterfaceRoleDim.getDimId().equals(qaInterfaceDim.getId())).findAny().orElse(null);

            Integer IsDisplay = vo == null ? 0 : vo.getIsDisplay();
            if (IsDisplay.equals(0)) i.remove();
        }
        return qaInterfaceDims;
    }

    private String getJsonNodeText(JsonNode node, String nodeName) {
        String nodeValue;
        if (node.has(nodeName)) {
            nodeValue = node.get(nodeName).asText();
        } else {
            nodeValue = Strings.EMPTY;
            logger.warn("nodeName = {} nodeValue ={} is Empty", nodeName, nodeValue);
        }
        return nodeValue;
    }

    private boolean negativeModelCheck(List<String> qaHitModels) {
        List<String> psaeNegativeModels = psaeConfig.getPsaeNegativeModels();
        boolean isContains = false;
        for (String o : psaeNegativeModels) {
            isContains = qaHitModels.stream().anyMatch(i -> i.equalsIgnoreCase(o));
            if (isContains) break;

        }
        return isContains;
    }

    private boolean positiveModelCheck(List<String> qaNotHitModels) {
        List<String> psaePositiveModels = psaeConfig.getPsaePositiveModels();
        boolean isContains = false;
        for (String o : psaePositiveModels) {
            isContains = qaNotHitModels.stream().anyMatch(i -> i.equalsIgnoreCase(o));
            if (isContains) break;

        }
        return !isContains;
    }

    private List<String> removePositiveModel(List<String> models) {
        Iterator<String> iterator = models.iterator();
        while (iterator.hasNext()) {
            String temp = iterator.next();
            boolean isExist = psaeConfig.getPsaePositiveModels().stream().anyMatch(vm -> vm.equalsIgnoreCase(temp));
            if (isExist) iterator.remove();
        }
        return models;
    }

    private List<String> removeNegativeModel(List<String> models) {
        Iterator<String> iterator = models.iterator();
        while (iterator.hasNext()) {
            String temp = iterator.next();
            boolean isExist = psaeConfig.getPsaeNegativeModels().stream().anyMatch(vm -> vm.equalsIgnoreCase(temp));
            if (isExist) iterator.remove();
        }
        return models;
    }

}
