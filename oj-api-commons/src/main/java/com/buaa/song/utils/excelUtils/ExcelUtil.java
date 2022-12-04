package com.buaa.song.utils.excelUtils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.buaa.song.exception.ExcelFormatException;
import com.buaa.song.utils.model.ExcelReadModel;
import com.buaa.song.utils.model.ExcelWriteModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @FileName: FileUtil
 * @author: ProgrammerZhao
 * @Date: 2020/11/11
 * @Description:
 */
@Component
public class ExcelUtil {

    public static ExcelReadListener readExcelFile(InputStream inputStream){
        ExcelReadListener listener = new ExcelReadListener();
        EasyExcel.read(inputStream, ExcelReadModel.class, listener).sheet().doRead();
        return listener;
    }

    public static String writeExcelFile(List<ExcelWriteModel> writeData, String outputFilePath){
        long millis = System.currentTimeMillis();
        String time = String.valueOf(millis);
        EasyExcel.write(outputFilePath+"OUT"+time+".xlsx", ExcelWriteModel.class).sheet().doWrite(writeData);
        return "OUT"+time+".xlsx";
    }

    public static void checkExcelFile(ExcelReadListener listener) throws ExcelFormatException {
        Map<Integer, String> head = listener.getHead();
        List<ExcelReadModel> data = listener.getData();
        if(head.size() < 2){
            throw new ExcelFormatException("Excel表头不能少于两列");
        }else if(!"学号".equals(head.get(0))){
            throw new ExcelFormatException("Excel第一列必须为学号");
        }else if(!"姓名".equals(head.get(1))){
            throw new ExcelFormatException("Excel第二列必须为姓名");
        }
        for(ExcelReadModel model : data){
            if(model.getStudentId() == null){
                throw new ExcelFormatException("Excel中存在学号为空的行");
            }else if(model.getName() == null){
                throw new ExcelFormatException("Excel中存在姓名为空的行");
            }
        }
    }

    public static void main(String[] args) {
        List <ExcelWriteModel> data = new ArrayList<>();
        for(int i = 0;i < 3;i++){
            ExcelWriteModel model = new ExcelWriteModel();
            model.setStudentId("1234");
            model.setName("名字");
            model.setIsAuth("是");
            model.setUsername(String.valueOf(i));
            model.setPassword(null);
            data.add(model);
        }
        writeExcelFile(data,"E:\\");
    }
}