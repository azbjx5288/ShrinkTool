package com.shrinktool.data;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 * 与MultipartRequest配合，支持文件上传
 * Created by Alashi on 2017/2/2.
 */

public class MultipartEntity{
    private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    /** 换行符 */
    private final String NEW_LINE_STR = "\r\n";
    private final String CONTENT_TYPE = "Content-Type: ";
    private final String CONTENT_DISPOSITION = "Content-Disposition: ";
    /** 文本参数和字符集 */
    private final String TYPE_TEXT_CHARSET = "text/plain; charset=UTF-8";
    /** 字节流参数 */
    private final String TYPE_OCTET_STREAM = "application/octet-stream";
    /** 二进制参数 */
    private final byte[] BINARY_ENCODING = "Content-Transfer-Encoding: binary\r\n\r\n".getBytes();
    /** 文本参数 */
    private final byte[] BIT_ENCODING = "Content-Transfer-Encoding: 8bit\r\n\r\n".getBytes();

    /** 分隔符 */
    private String boundary = null;
    /** 输出流 */
    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    public MultipartEntity() {
        this.boundary = generateBoundary();
    }

    /** 生成分隔符*/
    private String generateBoundary() {
        final StringBuilder buf = new StringBuilder();
        final Random rand = new Random();
        buf.append("----WebKitFormBoundary");
        for (int i = 0; i < 16; i++) {
            buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }

        return buf.toString();
    }

    /** 参数开头的分隔符*/
    private void writeFirstBoundary() throws IOException {
        outputStream.write(("--" + boundary + "\r\n").getBytes());
    }

    /** 添加文本参数 */
    public void addStringPart(final String paramName, final String value) {
        writeToOutputStream(paramName, value.getBytes(), TYPE_TEXT_CHARSET, BIT_ENCODING, "");
    }

    /** 将数据写入到输出流中*/
    private void writeToOutputStream(String paramName, byte[] rawData, String type, byte[] encodingBytes,
                                     String fileName) {
        try {
            writeFirstBoundary();
            outputStream.write(getContentDispositionBytes(paramName, fileName));
            outputStream.write((CONTENT_TYPE + type + NEW_LINE_STR).getBytes());
            if (encodingBytes != null) {
                outputStream.write(encodingBytes);
            }
            outputStream.write(rawData);
            outputStream.write(NEW_LINE_STR.getBytes());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /** 添加二进制参数, 例如Bitmap的字节流参数*/
    public void addBinaryPart(String paramName, final byte[] rawData) {
        writeToOutputStream(paramName, rawData, TYPE_OCTET_STREAM, BINARY_ENCODING, "no-file");
    }

    /**
     * 添加二进制参数, 例如Bitmap的字节流参数
     * addBinaryPart("headimg", image, "image/png", null, "??.png");
     * */
    public void addBinaryPart(String paramName, byte[] rawData, String type,
                              String encoding, String fileName) {
        byte[] encodingBytes = null;
        if (encoding != null) {
            encodingBytes = (encoding + "\r\n\r\n").getBytes();
        } else {
            encodingBytes = "\r\n".getBytes();
        }
        writeToOutputStream(paramName, rawData, type, encodingBytes, fileName);
    }

    /** 添加文件参数,可以实现文件上传功能*/
    public void addFilePart(final String key, final File file) {
        InputStream fin = null;
        try {
            fin = new FileInputStream(file);
            writeFirstBoundary();
            final String type = CONTENT_TYPE + TYPE_OCTET_STREAM + NEW_LINE_STR;
            outputStream.write(getContentDispositionBytes(key, file.getName()));
            outputStream.write(type.getBytes());
            outputStream.write(BINARY_ENCODING);

            final byte[] tmp = new byte[4096];
            int len;
            while ((len = fin.read(tmp)) != -1) {
                outputStream.write(tmp, 0, len);
            }
            outputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(fin);
        }
    }

    private void closeSilently(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] getContentDispositionBytes(String paramName, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(CONTENT_DISPOSITION);
        stringBuilder.append("form-data; name=\"");
        stringBuilder.append(paramName);
        stringBuilder.append("\"");
        // 文本参数没有filename参数,设置为空即可
        if (!TextUtils.isEmpty(fileName)) {
            stringBuilder.append("; filename=\"");
            stringBuilder.append(fileName);
            stringBuilder.append("\"");
        }

        return stringBuilder.append(NEW_LINE_STR).toString().getBytes();
    }

    public String getContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    public void writeTo(final OutputStream os) throws IOException {
        // 参数最末尾的结束符
        final String endString = "--" + boundary + "--\r\n";
        // 写入结束符
        outputStream.write(endString.getBytes());
        //
        os.write(outputStream.toByteArray());
    }
}
