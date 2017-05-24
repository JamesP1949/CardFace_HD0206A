package com.wis.utils;

import com.common.utils.Utils;
import com.opencsv.CSVWriter;
import com.socks.library.KLog;
import com.wis.bean.Person;
import com.wis.config.AppConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JamesP949 on 2017/5/10.
 * Function: export the DB-data into .csv file.
 */

public class ExportCsv {
    private static final String CHARSET = "UTF-8";

    public static String exportCsv(List<Person> data) throws IOException {
        String exportPath = "";
        OutputStreamWriter osw;
        CSVWriter writer;
        String filename = "比对记录" + Utils.dateFormat2String(System.currentTimeMillis()) + ".csv";
        if (data.isEmpty() || data == null) return exportPath;
        try {

            File file = new File(AppConfig.DB_ExportPath);
            if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
                KLog.e("创建文件夹成功--" + mkdirs);
            }
            exportPath = AppConfig.DB_ExportPath + File.separator + filename;
            KLog.e("exportPath---" + exportPath);
            File exportFile = new File(exportPath);
            String[] titles = new String[]{
                    "Id", "姓名", "性别", "民族", "身份证号", "地址",
                    "比对时间", "证件照路径", "现场照路径"
            };

            osw = new OutputStreamWriter(new FileOutputStream(exportFile), CHARSET);
            writer = new CSVWriter(osw);
            writer.writeNext(titles, false);

            List<String[]> resource = new ArrayList<>();
            for (Person person : data) {
                resource.add(new String[]{
                        String.valueOf(person.getId()),
                        person.getName(),
                        person.getSex(),
                        person.getNation(),
                        person.getCardId(),
                        person.getAddress(),
                        Utils.dateformat_2String(person.getDetectTime()),
                        person.getIdCardPhotoPath(),
                        person.getDetectPhotoPath()
                });
            }
            writer.writeAll(resource, false);
            writer.flushQuietly();
            writer.close();
        } finally {
            KLog.e("导出成功！！！");
            return exportPath;
        }
    }
}
