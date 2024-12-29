package com.consiliuminc.sras.service;

import com.consiliuminc.sras.configs.PsaeConfig;
import com.consiliuminc.sras.entities.postgres.QaInterfaceDim;
import com.consiliuminc.sras.entities.postgres.QaInterfaceRoleDim;
import com.consiliuminc.sras.entities.postgres.QaTaskJob;
import com.consiliuminc.sras.service.postgres.QaInterfaceDimService;
import com.consiliuminc.sras.service.postgres.QaInterfaceRoleDimService;
import com.consiliuminc.sras.service.postgres.QaTaskJobService;
import com.consiliuminc.sras.util.DateUtil;
import com.consiliuminc.sras.util.JacksonUtils;
import com.consiliuminc.sras.vo.StandardModelGroupVo;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.*;
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
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    private final String TOTAL_KEY = "總計";

    private QaTaskJobService qaTaskJobService;

    private QaInterfaceDimService qaInterfaceDimService;

    private QaModelService qaModelService;

    private QaInterfaceRoleDimService qaInterfaceRoleDimService;

    private PsaeConfig psaeConfig;

    private TaskExecutor taskReportExecutor;

    private static List<String> reRejectConditions = Arrays.asList("資料有誤", "TL要求退件", "客戶反悔投保", "客戶需求");

    private ThreadPoolTaskExecutor task;

    @Autowired
    public ReportService(QaTaskJobService qaTaskJobService, QaInterfaceDimService qaInterfaceDimService, QaModelService qaModelService, QaInterfaceRoleDimService qaInterfaceRoleDimService, PsaeConfig psaeConfig, @Qualifier("taskReportExecutor") TaskExecutor taskReportExecutor) {
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
        String _month = month.replace("-", "");
        LocalDate start = DateUtil.toLocalDate(sales_sdate);
        LocalDate end = DateUtil.toLocalDate(sales_edate);
//        LocalDate start = DateUtil.toLocalDate(String.format("%s%s", _month, "01"));
//        String lengthOfMonth = String.valueOf(start.lengthOfMonth());
//        LocalDate end = DateUtil.toLocalDate(String.format("%s%s", _month, lengthOfMonth));
        return this.exportReport(month, start, end);
    }


    //@Scheduled(fixedDelay = 10000)
    public void showTaskExecutorInfo() {
        logger.info("taskReportExecutor 當前活動線程數={} ,taskReportExecutor 線程處理隊列長度 ={}", task.getActiveCount(), task.getThreadPoolExecutor().getQueue().size());
    }

    private ByteArrayOutputStream exportReport(String month, LocalDate start, LocalDate end) throws Exception {


        logger.info("exportReport begin LocalDate start= {} end ={} ", start, end);
        //String businessMonth = start.format(DateTimeFormatter.ofPattern("yyyyMM"));
        String businessMonth = month;
        // FileInputStream file = new FileInputStream(loadReportFile());
        Workbook workbook = new XSSFWorkbook(loadReportStream());

        List<QaInterfaceDim> qaInterfaceDims = this.getQaInterfaceDims();

        List<StandardModelGroupVo> standardModelGroupVos = getStandardModelList(workbook.getSheetAt(5));
        List<QaTaskJob> qaTaskJobs = qaTaskJobService.findByTaskTLSubmitTime(start, end);


        //Not Async

        //RC report  parameter
//            Integer messageSendEventQuantity = 0;
//            Integer rcEventQuantity = 0;
//            Integer rejectEventQuantity = 0;
//            Map<String, Integer> rcRejectReasonCount = getRejectEventCondition();
//            //CM report parameter
//            Integer autoCriticalQuantity = 0, autoQualifiedQuantity = 0, autoConfirmQuantity = 0;
//            Integer reSalesQuantity = 0;
//            Integer lackOfOpenQuantity = 0, openAsAssetQuantity = 0, ensureUnclearQuantity = 0;
//            //CM Date report parameter
//            Integer excelIndex = 1;
//            List<List<StandardModelGroupVo>> cmDateReportDetailGroupModel = new ArrayList<>();

        //Async
        // RC report  parameter
        Map<String, Integer> rcRejectReasonCount = getRejectEventCondition();
        Map<String, Integer> rcReportMap = new HashMap<>();
        rcReportMap.put("messageSendEventQuantity", 0);
        rcReportMap.put("rcEventQuantity", 0);
        rcReportMap.put("rejectEventQuantity", 0);

        //CM report parameter
        Map<String, Integer> cmReportMap = new HashMap<>();
        cmReportMap.put("autoCriticalQuantity", 0);
        cmReportMap.put("autoQualifiedQuantity", 0);
        cmReportMap.put("autoConfirmQuantity", 0);
        cmReportMap.put("reSalesQuantity", 0);
        cmReportMap.put("lackOfOpenQuantity", 0);
        cmReportMap.put("openAsAssetQuantity", 0);
        cmReportMap.put("ensureUnclearQuantity", 0);
        //CM Date report parameter
        Integer excelIndex = 1;
        List<List<StandardModelGroupVo>> cmDateReportDetailGroupModel = new ArrayList<>();
        setCMDateReportHeader(workbook.getSheetAt(4), standardModelGroupVos, qaInterfaceDims);



        AtomicInteger processQuantity = new AtomicInteger(0);

        for (QaTaskJob qaTaskJob : qaTaskJobs) {

            Integer finalExcelIndex = excelIndex;
            taskReportExecutor.execute(() -> {
                try {
                    //取得CM Date Report 其他違規態樣紀錄;
                    List<StandardModelGroupVo> vos = getOtherConfirmAnalyze(qaTaskJob, standardModelGroupVos);
                    cmDateReportDetailGroupModel.add(vos);
                    setCMDateReportData(finalExcelIndex, workbook.getSheetAt(4), qaTaskJob, vos, qaInterfaceDims);

                    synchronized (this) {
                        JsonNode taskNode = JacksonUtils.toJsonNode(qaTaskJob.getTask());

                        //計算RC報表
                        rcReportMap.put("messageSendEventQuantity", rcReportMap.get("messageSendEventQuantity") + getMessageSendEvent(taskNode));
                        rcReportMap.put("rcEventQuantity", rcReportMap.get("rcEventQuantity") + getRcEvent(taskNode));
                        rcReportMap.put("rejectEventQuantity", rcReportMap.get("rejectEventQuantity") + getRejectEvent(taskNode, rcRejectReasonCount));


                        //計算CM報表

                        cmReportMap.put("autoCriticalQuantity", cmReportMap.get("autoCriticalQuantity") + getAutoCriticalEvent(qaTaskJob));
                        cmReportMap.put("autoQualifiedQuantity", cmReportMap.get("autoQualifiedQuantity") + getAutoQualifiedEvent(taskNode));
                        cmReportMap.put("autoConfirmQuantity", cmReportMap.get("autoConfirmQuantity") + getAutoConfirmEvent(taskNode));


                        cmReportMap.put("reSalesQuantity", cmReportMap.get("reSalesQuantity") + getReSalesEvent(taskNode));
                        cmReportMap.put("lackOfOpenQuantity", cmReportMap.get("lackOfOpenQuantity") + getCriticalAnalyze(qaTaskJob, psaeConfig.getLackOfOpenModelName()));
                        cmReportMap.put("openAsAssetQuantity", cmReportMap.get("openAsAssetQuantity") + getCriticalAnalyze(qaTaskJob, psaeConfig.getLackOfOpenModelName()));
                        cmReportMap.put("ensureUnclearQuantity", cmReportMap.get("ensureUnclearQuantity") + getCriticalAnalyze(qaTaskJob, psaeConfig.getLackOfOpenModelName()));

                        logger.info("task size={} , task current execute={}", qaTaskJobs.size(), finalExcelIndex);
                        processQuantity.getAndIncrement();
                    }


                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            });
            excelIndex++;
            while (task.getThreadPoolExecutor().getQueue().size() == task.getMaxPoolSize()) {

            }


//            logger.info("task size={} , task current execute={}", qaTaskJobs.size(), excelIndex);
//            //取得CM Date Report 其他違規態樣紀錄;
//            List<StandardModelGroupVo> vos = getOtherConfirmAnalyze(qaTaskJob, standardModelGroupVos);
//            cmDateReportDetailGroupModel.add(vos);
//
//            setCMDateReportData(excelIndex, workbook.getSheetAt(4), qaTaskJob, vos, qaInterfaceDims);
//
//            excelIndex++;
//            JsonNode taskNode = JacksonUtils.toJsonNode(qaTaskJob.getTask());
//            //計算RC報表
//            messageSendEventQuantity += getMessageSendEvent(taskNode);
//            rcEventQuantity += getRcEvent(taskNode);
//            rejectEventQuantity += getRejectEvent(taskNode, rcRejectReasonCount);
//
//            //計算CM報表
//            autoCriticalQuantity += getAutoCriticalEvent(qaTaskJob);
//            autoQualifiedQuantity += getAutoQualifiedEvent(taskNode);
//            autoConfirmQuantity += getAutoConfirmEvent(taskNode);
//
//            reSalesQuantity += getReSalesEvent(taskNode);
//
//            lackOfOpenQuantity += getCriticalAnalyze(qaTaskJob, psaeConfig.getLackOfOpenModelName());
//            openAsAssetQuantity += getCriticalAnalyze(qaTaskJob, psaeConfig.getOpenAsAssetModelName());
//            ensureUnclearQuantity += getCriticalAnalyze(qaTaskJob, psaeConfig.getEnsureUnclearModelName());

            //   setConfirmAnalyzeData(qaTaskJob, standardModelGroupVos);


        }

        while (task.getActiveCount() > 0 ) {

        }
       // Thread.sleep(2000);

//        Map<String, Integer> rcReportMap = new HashMap<>();
//        rcEventQuantity -= messageSendEventQuantity;
//        rcReportMap.put("messageSendEventQuantity", messageSendEventQuantity);
//        rcReportMap.put("rcEventQuantity", rcEventQuantity);
//        rcReportMap.put("rejectEventQuantity", rejectEventQuantity);
//
//
//        Map<String, Integer> cmReportMap = new HashMap<>();
//        cmReportMap.put("autoCriticalQuantity", autoCriticalQuantity);
//        cmReportMap.put("autoQualifiedQuantity", autoQualifiedQuantity);
//        cmReportMap.put("autoConfirmQuantity", autoConfirmQuantity);
//        cmReportMap.put("reSalesQuantity", reSalesQuantity);
//        cmReportMap.put("lackOfOpenQuantity", lackOfOpenQuantity);
//        cmReportMap.put("openAsAssetQuantity", openAsAssetQuantity);
//        cmReportMap.put("ensureUnclearQuantity", ensureUnclearQuantity);

        rcReportMap.put("rcEventQuantity", rcReportMap.get("rcEventQuantity") - rcReportMap.get("messageSendEventQuantity"));
        setRcReportData(workbook.getSheetAt(0), businessMonth, rcReportMap, rcRejectReasonCount);
        //setCMReportData(workbook.getSheetAt(1), businessMonth, cmReportMap, standardModelGroupVos);
        setCMReportData(workbook.getSheetAt(1), businessMonth, cmReportMap, cmDateReportDetailGroupModel);

        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
        workbook.write(bOutput);
        logger.info("exportReport finish LocalDate start= {} end ={} ", start, end);
        return bOutput;

    }

    private void setCMDateReportHeader(Sheet sheet, List<StandardModelGroupVo> standardModelGroupVos, List<QaInterfaceDim> qaInterfaceDims) {
        Integer i = 0, cellIndex = 0;
        //List<QaInterfaceDim> qaInterfaceDims = qaInterfaceDimService.findAllOrderByOrder();
        //List<QaInterfaceDim> qaInterfaceDims = this.getQaInterfaceDims();
        Row row = sheet.getRow(0);
        Cell cell = row.getCell(0);
        CellStyle style = cell.getCellStyle();
        for (i = 0; i < qaInterfaceDims.size(); i++) {
            cell = row.createCell(i);
            cell.setCellValue(qaInterfaceDims.get(i).getDisplay_name());
            cell.setCellStyle(style);
        }

        for (int j = 0; j < standardModelGroupVos.size(); j++) {
            cell = row.createCell(i);
            cell.setCellValue(standardModelGroupVos.get(j).getGroupName());
            cell.setCellStyle(style);
            i++;
        }
        cell = row.createCell(i);
        cell.setCellValue("瑕疵數總計");
        cell.setCellStyle(style);
    }

    //產生CM DATE 報表　並且統計個別待覆核數量
    private void setCMDateReportData(Integer excelIndex, Sheet sheet, QaTaskJob qaTaskJob, List<StandardModelGroupVo> vos, List<QaInterfaceDim> qaInterfaceDims) throws IOException, NoSuchFieldException, IllegalAccessException {

        String fieldName, fieldValue, type;
        Integer i = 0, total = 0;
        Class<?> cls = qaTaskJob.getClass();
        //List<QaInterfaceDim> qaInterfaceDims = qaInterfaceDimService.findAllOrderByOrder();
        Row row = sheet.createRow(excelIndex);
        Cell cell = null;
        // List<QaInterfaceDim> qaInterfaceDims = this.getQaInterfaceDims();
        JsonNode taskNode = JacksonUtils.toJsonNode(qaTaskJob.getTask());
        for (i = 0; i < qaInterfaceDims.size(); i++) {
            fieldName = qaInterfaceDims.get(i).getField_name();
            type = qaInterfaceDims.get(i).getType();
            if ("task".equalsIgnoreCase(type)) {
                fieldValue = getJsonNodeText(taskNode, fieldName);
            } else {
                Field field = cls.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(qaTaskJob);
                fieldValue = value == null ? Strings.EMPTY : value.toString();
            }
            cell = row.createCell(i);
            cell.setCellValue(fieldValue);
        }

//        Map<String, Integer> map = getOtherConfirmAnalyze(qaTaskJob, standardModelGroupVos);
//        for (Map.Entry<String, Integer> entry : map.entrySet()) {
//            total += entry.getValue();
//            cell = row.createCell(i++);
//            cell.setCellValue(entry.getValue());
//
//        }
        // List<StandardModelGroupVo> vos = getOtherConfirmAnalyze(qaTaskJob, standardModelGroupVos);
        for (StandardModelGroupVo standardModelGroupVo : vos) {
            total += standardModelGroupVo.getCount();
            cell = row.createCell(i++);
            cell.setCellValue(standardModelGroupVo.getCount());

        }
        cell = row.createCell(i);
        cell.setCellValue(total);

    }


    private void setCMReportData(Sheet sheet, String businessMonth, Map<String, Integer> cmReportMap, List<List<StandardModelGroupVo>> standardModelGroupVos) {
        Integer autoCriticalQuantity = cmReportMap.get("autoCriticalQuantity");
        Integer autoQualifiedQuantity = cmReportMap.get("autoQualifiedQuantity");
        Integer autoConfirmQuantity = cmReportMap.get("autoConfirmQuantity");
        Integer reSalesQuantity = cmReportMap.get("reSalesQuantity");
        Integer lackOfOpenQuantity = cmReportMap.get("lackOfOpenQuantity");
        Integer openAsAssetQuantity = cmReportMap.get("openAsAssetQuantity");
        Integer ensureUnclearQuantity = cmReportMap.get("ensureUnclearQuantity");


        Integer autoTotal = autoCriticalQuantity + autoQualifiedQuantity + autoConfirmQuantity;
        Integer total = lackOfOpenQuantity + openAsAssetQuantity + ensureUnclearQuantity;

        float autoCriticalQuantityProportion = autoCriticalQuantity / (autoTotal == 0 ? 1.0f : autoTotal);
        float autoQualifiedQuantityProportion = autoQualifiedQuantity / (autoTotal == 0 ? 1.0f : autoTotal);
        float autoConfirmQuantityProportion = autoConfirmQuantity / (autoTotal == 0 ? 1.0f : autoTotal);

        float lackOfOpenQuantityProportion = lackOfOpenQuantity / (total == 0 ? 1.0f : total);
        float openAsAssetQuantityQuantityProportion = openAsAssetQuantity / (total == 0 ? 1.0f : total);
        float ensureUnclearQuantityQuantityProportion = ensureUnclearQuantity / (total == 0 ? 1.0f : total);


        sheet.getRow(5).getCell(1).setCellValue(businessMonth);


        sheet.getRow(10).getCell(1).setCellValue(autoCriticalQuantity); //重大瑕疵(開場白缺漏)
        sheet.getRow(10).getCell(2).setCellValue(getPercentData(autoCriticalQuantityProportion)); //重大瑕疵(開場白缺漏) 占比

        sheet.getRow(11).getCell(1).setCellValue(autoQualifiedQuantity); //合格
        sheet.getRow(11).getCell(2).setCellValue(getPercentData(autoQualifiedQuantityProportion)); //合格 占比

        sheet.getRow(12).getCell(1).setCellValue(autoConfirmQuantity); //其他待覆核
        sheet.getRow(12).getCell(2).setCellValue(getPercentData(autoConfirmQuantityProportion)); //其他待覆核 占比

        sheet.getRow(13).getCell(1).setCellValue(autoTotal);
        sheet.getRow(13).getCell(2).setCellValue(getPercentData(1));

        sheet.getRow(20).getCell(1).setCellValue(reSalesQuantity); //重大瑕疵退件重新銷售件數


        sheet.getRow(23).getCell(1).setCellValue(lackOfOpenQuantity); //開場白缺漏
        sheet.getRow(23).getCell(2).setCellValue(getPercentData(lackOfOpenQuantityProportion)); //開場白缺漏 占比

        sheet.getRow(24).getCell(1).setCellValue(openAsAssetQuantity); //以累積財富或利率為開場
        sheet.getRow(24).getCell(2).setCellValue(getPercentData(openAsAssetQuantityQuantityProportion)); //以累積財富或利率為開場 占比

        sheet.getRow(25).getCell(1).setCellValue(ensureUnclearQuantity); //保障/權益說明不清
        sheet.getRow(25).getCell(2).setCellValue(getPercentData(ensureUnclearQuantityQuantityProportion)); //保障/權益說明不清 占比

        sheet.getRow(26).getCell(1).setCellValue(total);
        sheet.getRow(26).getCell(2).setCellValue(getPercentData(1));


        Integer startIndex = 35, violationTotal = 0, index;
        boolean IsInitial = true;
        float proportion = 0.f;
        StandardModelGroupVo vo = null;
        List<Integer> sumList = new ArrayList<>();
        for (List<StandardModelGroupVo> vos : standardModelGroupVos) {
            index = 0;
            if (IsInitial) {
                for (int i = 0; i < vos.size(); i++) {
                    sumList.add(0);
                }
            }
            for (StandardModelGroupVo v : vos) {
                if (IsInitial) sheet.getRow(startIndex).getCell(0).setCellValue(v.getGroupName());
                sumList.set(index, sumList.get(index) + v.getCount());
                index++;
                startIndex++;
            }
            IsInitial = false;
        }

        violationTotal = sumList.stream().mapToInt(Integer::intValue).sum();
        startIndex = 35;
        for (Integer sum : sumList) {
            proportion = sum / (violationTotal == 0 ? 1.0f : violationTotal);
            sheet.getRow(startIndex).getCell(1).setCellValue(sum);
            sheet.getRow(startIndex).getCell(2).setCellValue(getPercentData(proportion)); // 占比
            startIndex++;
        }

//        boolean IsFirstExecute = true;
//        for (Integer index = 0; index < standardModelGroupVos.size(); index++) {
//            vo = standardModelGroupVos.get(index);
//
//            if (IsFirstExecute) {
//                violationTotal += vo.getCount();
//                sheet.getRow(startIndex).getCell(0).setCellValue(vo.getGroupName());
//                sheet.getRow(startIndex).getCell(1).setCellValue(vo.getCount());
//            } else {
//                proportion = vo.getCount() / (violationTotal == 0 ? 1.0f : violationTotal);
//                sheet.getRow(startIndex).getCell(2).setCellValue(getPercentData(proportion)); //保障/權益說明不清 占比
//            }
//            ++startIndex;
//            if (index == standardModelGroupVos.size() - 1) {
//                if (!IsFirstExecute) break;
//                index = -1;
//                IsFirstExecute = false;
//                startIndex = 35;
//            }
//        }

        sheet.getRow(startIndex).getCell(0).setCellValue(TOTAL_KEY);
        sheet.getRow(startIndex).getCell(1).setCellValue(violationTotal);
        sheet.getRow(startIndex).getCell(2).setCellValue(getPercentData(1));

    }


    private void setRcReportData(Sheet sheet, String businessMonth, Map<String, Integer> rcReportMap, Map<String, Integer> rcRejectReasonCount) {

        Integer messageSendEventQuantity = rcReportMap.get("messageSendEventQuantity"); //簡訊發送總數
        Integer rcEventQuantity = rcReportMap.get("rcEventQuantity"); //RC總數
        Integer rejectEventQuantity = rcReportMap.get("rejectEventQuantity"); //RC退件總數
        Integer total = messageSendEventQuantity + rcEventQuantity;
        float rcProportion = rcEventQuantity / (total == 0 ? 1.0f : total);
        float rcRejectProportion = rejectEventQuantity / (total == 0 ? 1.0f : total);
        float messageSendProportion = messageSendEventQuantity / (total == 0 ? 1.0f : total);
        float totalRejectProportion = rejectEventQuantity / (total == 0 ? 1.0f : total);


        sheet.getRow(3).getCell(1).setCellValue(businessMonth);
        sheet.getRow(18).getCell(1).setCellValue(businessMonth);


        sheet.getRow(7).getCell(1).setCellValue(rcEventQuantity); //RC件數
        sheet.getRow(7).getCell(2).setCellValue(rejectEventQuantity); //RC退件數


        sheet.getRow(7).getCell(3).setCellValue(getPercentData(rcProportion)); //RC占比
        sheet.getRow(7).getCell(4).setCellValue(getPercentData(rcRejectProportion)); //RC退件占比

        sheet.getRow(8).getCell(1).setCellValue(messageSendEventQuantity); //簡訊發送
        sheet.getRow(8).getCell(3).setCellValue(getPercentData(messageSendProportion)); //簡訊占比

        sheet.getRow(9).getCell(1).setCellValue(total); //總計
        sheet.getRow(9).getCell(2).setCellValue(rejectEventQuantity); //總計退件
        sheet.getRow(9).getCell(3).setCellValue(getPercentData(1f)); //總計占比
        sheet.getRow(9).getCell(4).setCellValue(getPercentData(totalRejectProportion)); //總計退件占比


        Integer start = 21;
        total = 0;
        for (Map.Entry<String, Integer> entry : rcRejectReasonCount.entrySet()) {
            Integer quantity = entry.getValue();
            float proportion = quantity / (rcEventQuantity == 0 ? 1.0f : rcEventQuantity);
            sheet.getRow(start).getCell(1).setCellValue(quantity);
            sheet.getRow(start).getCell(2).setCellValue(getPercentData(proportion)); //總計退件占比
            total += quantity;
            start++;
        }
        sheet.getRow(start).getCell(1).setCellValue(total);
        sheet.getRow(start).getCell(2).setCellValue(getPercentData(1f)); //總計退件占比
    }


    private List<StandardModelGroupVo> getOtherConfirmAnalyze(QaTaskJob qaTaskJob, List<StandardModelGroupVo> standardModelGroupVos) {

        StandardModelGroupVo detailVo;
        List<StandardModelGroupVo> detailModelGroupVo = new ArrayList<>();
        Integer jobId = qaTaskJob.getId();
        //List<String> qaHitModels = qaModelService.getAllHitQaModel(jobId);

//        List<String> qaHitModels = qaModelService.getAllQaModelByHumanHitIsTrue(jobId);
//        List<String> qaNotHitModels = qaModelService.getAllQaModelByHumanHitIsFalse(jobId);
//
//        qaHitModels = removePositiveModel(qaHitModels);
//        qaNotHitModels = removeNegativeModel(qaNotHitModels);

//        List<String> qaHitModels = qaModelService.getAllQaGroupModelByHumanHit(jobId, "true");
//        List<String> qaNotHitModels = qaModelService.getAllQaGroupModelByHumanHit(jobId, "false");

        Map<Boolean, List<String>> map = qaModelService.getAllQaGroupModel(jobId);
        List<String> qaHitModels = map.get(true);
        List<String> qaNotHitModels = map.get(false);
        qaHitModels = removePositiveModel(qaHitModels);
        qaNotHitModels = removeNegativeModel(qaNotHitModels);

        boolean IsExistNegativeModels = negativeModelCheck(qaHitModels);
        boolean IsExistPositiveModels = positiveModelCheck(qaNotHitModels);


        for (StandardModelGroupVo vo : standardModelGroupVos) {
            detailVo = new StandardModelGroupVo(0, vo.getGroupName(), vo.getGroupModels());
            detailModelGroupVo.add(detailVo);
            List<String> groupModels = vo.getGroupModels();
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
        return detailModelGroupVo;
    }


    private Integer getCriticalAnalyze(QaTaskJob qaTaskJob, String modelName) {
        Integer cal = 0;
        Integer jobId = qaTaskJob.getId();
        List<String> models = qaModelService.getAllQaModelByHumanHitIsTrue(jobId);
        boolean IsExist = models.stream().anyMatch(q -> q.equalsIgnoreCase(modelName));
        if (IsExist) cal = 1;
        return cal;

    }


    //重大瑕疵退件重新銷售件數
    private Integer getReSalesEvent(JsonNode taskNode) {

        String resales = getJsonNodeText(taskNode, "psae_ReSales");
        if ("Y".equals(resales))
            return 1;
        else
            return 0;
    }


    //自動重大瑕疵
    private Integer getAutoCriticalEvent(QaTaskJob qaTaskJob) {
        Integer state = qaTaskJob.getState();
        String case_content = qaTaskJob.getCase_content();
        if (state.equals(1000) && psaeConfig.getCriticalKey().equalsIgnoreCase(case_content)) {
            return 1;
        } else
            return 0;
    }

    //自動合格
    private Integer getAutoQualifiedEvent(JsonNode taskNode) {
        String tmrPendingCode1Name = getJsonNodeText(taskNode, "qa_result");//質檢結果
        if ("優秀".equals(tmrPendingCode1Name))
            return 1;
        else
            return 0;
    }


    //自動待覆核
    private Integer getAutoConfirmEvent(JsonNode taskNode) {
        String tmrPendingCodeName = getJsonNodeText(taskNode, "psae_TMRPendingCodeName");//pendingCode項別名稱
        String tmrPendingCode2Name = getJsonNodeText(taskNode, "psae_TMRPendingCode2Name");//pendingCode小項名稱
        if (tmrPendingCodeName.contains("CM") && !"重大瑕疵重新銷售".equals(tmrPendingCode2Name) && !"同意投保".equals(tmrPendingCode2Name))
            return 1;
        else
            return 0;
    }


    //簡訊發送
    private Integer getMessageSendEvent(JsonNode taskNode) {
        String tmrPendingCode1Name = getJsonNodeText(taskNode, "psae_TMRPendingCode1Name");//taskNode.get("psae_TMRPendingCode1Name").asText(); //pendingCode大項名稱
        if ("簡訊發送".equals(tmrPendingCode1Name))
            return 1;
        else
            return 0;
    }

    //RC 件數
    private Integer getRcEvent(JsonNode taskNode) {
        String tmrPendingCodeName = getJsonNodeText(taskNode, "psae_TMRPendingCodeName"); //pendingCode項別名稱
        if ("RCF".equalsIgnoreCase(tmrPendingCodeName) || "CM".equalsIgnoreCase(tmrPendingCodeName) || "CM+RCF".equalsIgnoreCase(tmrPendingCodeName))
            return 1;
        else
            return 0;
    }

    //RC 退件數
    private Integer getRejectEvent(JsonNode taskNode, Map<String, Integer> conditions) {
        String tmrPendingCode1Name = getJsonNodeText(taskNode, "psae_TMRPendingCode1Name");//taskNode.get("psae_TMRPendingCode1Name").asText(); //pendingCode大項名稱
        Integer count = conditions.get(tmrPendingCode1Name);
        boolean isExist = count != null;
        if (isExist) {
            conditions.put(tmrPendingCode1Name, ++count);
            return 1;
        } else
            return 0;
    }


    private String getPercentData(float floatData) {
        return String.format("%.2f%s", floatData * 100, "%");
    }

    private Map<String, Integer> getRejectEventCondition() {
        Map<String, Integer> maps = new HashMap<>();
        reRejectConditions.forEach(c -> {
            maps.put(c, 0);
        });
        return maps;
    }


    private List<StandardModelGroupVo> getStandardModelList(Sheet sheet) {

        List<StandardModelGroupVo> data = new ArrayList<>();
        String groupName = Strings.EMPTY;
        List<String> groupModels;
        StandardModelGroupVo vo;
        for (Row row : sheet) {
            if (row.getRowNum() > 1) {
                try {
                    groupModels = new ArrayList<>(Arrays.asList(row.getCell(2).toString().split("\n")));
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
