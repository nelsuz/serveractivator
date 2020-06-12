package com.develoqu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

public class Base {
    File baseFile;
    BufferedWriter bufferedWriter;
    BufferedReader bufferedReader;
    MD5 hash;

    public Base (String file) throws IOException {
        baseFile = new File(file);
        bufferedWriter = new BufferedWriter(new FileWriter(baseFile, true));
        bufferedReader = new BufferedReader(new FileReader(baseFile));
        hash = new MD5();

        if(!baseFile.exists()){
            baseFile.createNewFile();
        }
    }

    public String addNewKey () throws IOException {
        int random_number = 10000 + (int) (Math.random() * 99999);
        int random_number1 = 1000 + (int) (Math.random() * 9999);
        String rand_key = random_number + "-" + random_number1;
        bufferedWriter.append(hash.md5Hash(rand_key) + "|#NA\n");
        bufferedWriter.flush();
        bufferedWriter.close();

        return rand_key;
    }

    public short checkKey (String key, String compHash) throws IOException {
        short keyStatus = 0;
        String buf;

        while((buf = bufferedReader.readLine()) != null){
            String[] line = buf.split("\\|");
            if(line[0].equals(hash.md5Hash(key))){
                if(line[1].equals("#NA")){
                    StringBuilder sb = new StringBuilder();
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(baseFile)))) {
                        String strLine;
                        while ((strLine = br.readLine()) != null) {
                            sb.append(strLine.replace(line[0] + "|" + line[1],
                                    line[0] + "|" + compHash)).append("\r\n");
                        }
                    }
                    try (FileWriter fileWriter = new FileWriter(baseFile)) {
                        fileWriter.write(sb.toString());
                    }

                    keyStatus = 1;
                } else if (line[1].equals(compHash)){
                    keyStatus = 2;
                }
            }
        }

        return keyStatus;
    }

}
