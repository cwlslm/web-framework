package com.jfeng.framework.mvc;

import com.jfeng.framework.core.ConfigHelper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import com.jfeng.framework.mvc.bean.RequestFileParam;
import com.jfeng.framework.mvc.bean.RequestQueryOrFormParam;
import com.jfeng.framework.mvc.bean.RequestParam;
import com.jfeng.framework.util.CollectionUtil;
import com.jfeng.framework.util.FileUtil;
import com.jfeng.framework.util.StreamUtil;
import com.jfeng.framework.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文件上传助手类
 *
 * @author jfeng
 * @since 1.0.0
 */
public class FileUploadHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadHelper.class);

    /**
     * Apache Commons FileUpload 提供的 Servlet 文件上传对象
     */
    private static ServletFileUpload servletFileUpload;

    /**
     * 初始化
     */
    public static void init(ServletContext servletContext) {
        File repository = (File) servletContext.getAttribute("javax.servlet.content.tempdir");
        servletFileUpload = new ServletFileUpload(
                new DiskFileItemFactory(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD, repository));
        int uploadLimit = ConfigHelper.getAppUploadLimit();
        if (uploadLimit != 0) {
            servletFileUpload.setFileSizeMax(uploadLimit * 1024 * 1024);
        }
    }

    /**
     * 判断请求是否为 multipart 类型
     */
    public static boolean isMultiPart(HttpServletRequest request) {
        return ServletFileUpload.isMultipartContent(request);
    }

    /**
     * 创建请求对象
     */
    public static RequestParam createParam(HttpServletRequest request) throws IOException {
        List<RequestQueryOrFormParam> queryParamList = RequestParamHelper.parseParameterNames(request);
        List<RequestQueryOrFormParam> formParamList = new ArrayList<>();
        List<RequestFileParam> fileParamList = new ArrayList<>();
        try {
            Map<String, List<FileItem>> fileItemListMap = servletFileUpload.parseParameterMap(request);
            if (CollectionUtil.isNotEmpty(fileItemListMap)) {
                for (Map.Entry<String, List<FileItem>> fileItemListEntry: fileItemListMap.entrySet()) {
                    String fieldName = fileItemListEntry.getKey();
                    List<FileItem> fileItemList = fileItemListEntry.getValue();
                    if (CollectionUtil.isNotEmpty(fileItemList)) {
                        for (FileItem fileItem: fileItemList) {
                            if (fileItem.isFormField()) {
                                String fieldValue = fileItem.getString("UTF-8");
                                formParamList.add(new RequestQueryOrFormParam(fieldName, fieldValue));
                            } else {
                                String fileName = FileUtil.getRealFileName(
                                        new String(fileItem.getName().getBytes(),
                                                StandardCharsets.UTF_8));
                                if (StringUtil.isNotEmpty(fileName)) {
                                    long fileSize = fileItem.getSize();
                                    String contentType = fileItem.getContentType();
                                    InputStream inputStream = fileItem.getInputStream();
                                    fileParamList.add(
                                            new RequestFileParam(fieldName, fileName, fileSize,
                                                    contentType, inputStream));
                                }
                            }
                        }
                    }
                }
            }
        } catch (FileUploadException e) {
            LOGGER.error("createParam 创建参数对象FileUploadException异常", e);
            throw new RuntimeException(e);
        }
        return new RequestParam(queryParamList, formParamList, fileParamList);
    }

    /**
     * 上传文件
     */
    public static void uploadFile(String basePath, RequestFileParam requestFileParam) {
        try {
            if (requestFileParam != null) {
                String filePath = basePath + requestFileParam.getFileName();
                FileUtil.createFile(filePath);
                InputStream inputStream = new BufferedInputStream(requestFileParam.getInputStream());
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath));
                StreamUtil.copyStream(inputStream, outputStream);
            }
        } catch (Exception e) {
            LOGGER.error("uploadFile 异常", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量上传文件
     */
    public static void uploadFile(String basePath, List<RequestFileParam> fileParamList) {
        try {
            if (CollectionUtil.isNotEmpty(fileParamList)) {
                for (RequestFileParam requestFileParam : fileParamList) {
                    uploadFile(basePath, requestFileParam);
                }
            }
        } catch (Exception e) {
            LOGGER.error("uploadFile 异常", e);
            throw new RuntimeException(e);
        }
    }
}
