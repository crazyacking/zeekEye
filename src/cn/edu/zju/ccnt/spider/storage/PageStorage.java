package cn.edu.zju.ccnt.spider.storage;

import cn.edu.zju.ccnt.spider.parser.WebDataExtraction;

import javax.sound.midi.SysexMessage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by crazyacking on 2015/6/28.
 */
public class PageStorage {
    public static String[] getFileName(String path) {
        File file = new File(path);
        String[] fileName = file.list();
        return fileName;
    }

    public static void getAllFileName(String path, ArrayList<String> fileName) {
        File file = new File(path);
        File[] files = file.listFiles();
        String[] names = file.list();
        if (names != null)
            fileName.addAll(Arrays.asList(names));
        for (File a : files) {
            if (a.isDirectory()) {
                getAllFileName(a.getAbsolutePath(), fileName);
            }
        }
    }

    public void WebPageStorage() throws IOException, InterruptedException {
        /*String[] fileName = getFileName("����");
        for (String name : fileName) {
            System.out.println(name);
        }
        System.out.println("--------------------------------");*/
        ArrayList<String> listFileName = new ArrayList<String>();
        getAllFileName("conf\\rootDisk\\temp", listFileName);
        for (String name : listFileName) {
            Thread.sleep(1000);
            String oldFilePath = "conf\\rootDisk\\temp\\" + name;
            FileInputStream fileInputStream = new FileInputStream(oldFilePath);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String contentString = null;
            String tempString = null;
            while ((tempString = bufferedReader.readLine()) != null) {
                contentString+=tempString;
                contentString+="\r\n";
            }


            String newFilePath="file\\WebPage\\"+name;
            FileOutputStream fileOutputStream=new FileOutputStream(newFilePath);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream,"UTF-8");
            BufferedWriter bufferedWriter=new BufferedWriter(outputStreamWriter);

            File newfile=new File(newFilePath);
            System.out.println(name + "    downloading ... ");

            if(!newfile.exists()){
                newfile.createNewFile();
            }

//            newFilePath=newFilePath + "\\"+name;
//            File newFileIn=new File(newFilePath);
//            BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(newFileIn));
            bufferedWriter.write(contentString, 0, contentString.length());
            WebDataExtraction webDataExtraction=new WebDataExtraction();
            webDataExtraction.BeginWebDataExtraction(contentString);


            bufferedWriter.flush();
            bufferedWriter.close();




            /*File oldFile = new File(oldFilePath);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(oldFile));
            String content = null;
            String tempString = null;
            while ((tempString = bufferedReader.readLine()) != null) {
                content += tempString;
            }*/
//            System.out.println(contentString);

        }
    /*static int pageNumber = 1;

    public static void contentStorage(String content) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder = stringBuilder.append(pageNumber);
        pageNumber++;
        String pageName = "WebPage";
        String tempString = null;
        String filePath = "file\\WebPage\\";
        String Suffix = ".spider";
        try {
            tempString = String.valueOf(stringBuilder);
            File dir = new File(filePath, pageName + tempString + Suffix);
            while (dir.exists()) {
                pageNumber++;
                stringBuilder = stringBuilder.append(pageNumber);
                tempString = String.valueOf(stringBuilder);
                dir = new File(filePath, pageName + tempString + Suffix);
            }

            if (!dir.exists()) {
                dir.createNewFile();
            }
        } catch (Exception e) {
            System.out.println(pageName + "File creation failed��");
        }
        System.out.println("downloading page : " + pageName + "...");
        filePath = filePath + pageName + tempString + Suffix;
        File file = new File(filePath);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        bufferedWriter.write(content, 0, content.length());
        bufferedWriter.flush();
        bufferedWriter.close();*/
    }
}
