package com.FileQuash.Machines;

import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class Quasher {

    private StringBuilder sb = new StringBuilder();
    private  Map<Byte, String> huffmap = new HashMap<>();

    private byte[] processedFileContent;

    public byte[] fileCompress(MultipartFile file) {
        try {
        	byte[] b = file.getBytes();
            byte[] huffManBytes = createZip(b);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            
            
            objectOutputStream.writeObject(huffManBytes);
            objectOutputStream.writeObject(huffmap);
            processedFileContent = byteArrayOutputStream.toByteArray();
            

            objectOutputStream.flush();
            objectOutputStream.close();
            
            
            return processedFileContent;
           
        }
        catch(Exception e) {
            System.out.println(e.toString());
        }
        
        return null;
    }

    private byte[] createZip(byte[] bytes) {
        PriorityQueue<ByteNode> nodes = getByteNodes(bytes);
        ByteNode root =createHuffmanTree(nodes);
        Map<Byte , String>  huffmanCodes = getHuffCodes(root);
        byte[] huffmanCodesBytes = zipBytesWithCodes(bytes,huffmanCodes);
        return huffmanCodesBytes;
    }

    private PriorityQueue<ByteNode> getByteNodes(byte[] bytes) {
        PriorityQueue<ByteNode> nodes = new PriorityQueue<ByteNode>();
        Map<Byte ,Integer> tempMap =new HashMap<>();

        
        for(byte b : bytes) {
            tempMap.put( b , tempMap.getOrDefault(b, 0) +1 );
        }
        for(Map.Entry<Byte , Integer> mp : tempMap.entrySet()) {
            nodes.add(new ByteNode(mp.getKey(), mp.getValue()));
        }
        return nodes;    
    }

    private ByteNode createHuffmanTree(PriorityQueue<ByteNode> nodes) {
        while (nodes.size() > 1) {
            ByteNode left = nodes.poll();
            ByteNode right = nodes.poll();
            ByteNode parent = new ByteNode(null, left.frequency + right.frequency);
            parent.left = left;
            parent.right = right;
            nodes.add(parent);
        }
        return nodes.poll();
    }

    private  Map<Byte , String> getHuffCodes(ByteNode root) {
        if(root == null) 
            return null;
        getHuffCodes(root.left, "0", sb);
        getHuffCodes(root.right, "1", sb);
        return huffmap;
    }

    //overloaded method of above 
    private void getHuffCodes(ByteNode root ,String code , StringBuilder sb1) {
        StringBuilder sb2 = new StringBuilder(sb1);
        sb2.append(code);
        if(root != null) {
            if(root.data == null) {
                getHuffCodes(root.left, "0", sb2);
                getHuffCodes(root.right, "1", sb2);
            }
            else   
                huffmap.put(root.data, sb2.toString());
        }
    }

    private byte[] zipBytesWithCodes(byte[] bytes, Map<Byte, String> huffCodes) {
        StringBuilder strBuilder = new StringBuilder();
        for (byte b : bytes)
            strBuilder.append(huffCodes.get(b));

        int length=(strBuilder.length()+7)/8;
        byte[] huffCodeBytes = new byte[length];
        int idx = 0;
        for (int i = 0; i < strBuilder.length(); i += 8) {
            String strByte;
            if (i + 8 > strBuilder.length())
                strByte = strBuilder.substring(i);
            else strByte = strBuilder.substring(i, i + 8);
            huffCodeBytes[idx] = (byte) Integer.parseInt(strByte, 2);
            idx++;
        }
        return huffCodeBytes;
    }

    public byte[] fileDecompress(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            ObjectInputStream objectInStream =new ObjectInputStream(inputStream);
            byte[] huffManBytes = (byte[]) objectInStream.readObject();
            Map<Byte,String> huffmanCodes = (Map<Byte ,String>) objectInStream.readObject();

            byte[] bytes =decomp(huffmanCodes , huffManBytes);
            inputStream.close();
            objectInStream.close();
            System.out.println("File decompressed successfully");
            return bytes;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

    private  byte[] decomp(Map<Byte, String> huffmanCodes, byte[] huffmanBytes) {
        StringBuilder sb1 = new StringBuilder();
        for (int i=0; i<huffmanBytes.length; i++) {
            byte b = huffmanBytes[i];
            boolean flag = (i == huffmanBytes.length - 1);
            sb1.append(convertbyteInBit(!flag, b));
        }
        Map<String, Byte> map = new HashMap<>();
        for (Map.Entry<Byte, String> entry : huffmanCodes.entrySet()) {
            map.put(entry.getValue(), entry.getKey());
        }
        java.util.List<Byte> list = new java.util.ArrayList<>();
        for (int i = 0; i < sb1.length();) {
            int count = 1;
            boolean flag = true;
            Byte b = null;
            while (flag) {
                String key = sb1.substring(i, i + count);
                b = map.get(key);
                if (b == null) count++;
                else flag = false;
            }
            list.add(b);
            i += count;
        }
        byte b[] = new byte[list.size()];
        for (int i = 0; i < b.length; i++)
            b[i] = list.get(i);
        return b;
    }

    private String convertbyteInBit(boolean flag, byte b) {
        int byte0 = b;
        if (flag) byte0 |= 256;
        String str0 = Integer.toBinaryString(byte0);
        if (flag || byte0 < 0)
            return str0.substring(str0.length() - 8);
        else return str0;
    }

}
