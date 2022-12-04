package com.buaa.song.utils.excelUtils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.buaa.song.utils.model.ExcelReadModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @FileName: ExcelReadEventListener
 * @author: ProgrammerZhao
 * @Date: 2020/11/11
 * @Description:
 */

public class ExcelReadListener extends AnalysisEventListener<ExcelReadModel> {

    private static final Logger logger = LoggerFactory.getLogger(ExcelReadListener.class);
    private Map<Integer, String> head;
    private List<ExcelReadModel> data;


    public ExcelReadListener(){
        data = new ArrayList<>();
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        head = headMap;
    }

    public void invoke(ExcelReadModel data, AnalysisContext context) {
        this.data.add(data);
    }

    public void doAfterAllAnalysed(AnalysisContext context) {

    }

    public Map<Integer, String> getHead() {
        return head;
    }

    public List<ExcelReadModel> getData() {
        return data;
    }


}