package com.springai.pi.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;

/**
 * 文件操作工具类
 */
@Slf4j
public class FileOperationTool {
    
    // 基础目录为用户目录下的tmp文件夹
    private static final String BASE_DIR = System.getProperty("user.dir") + File.separator + "tmp";

    /**
     * 读取文件内容
     *
     * @param fileName 文件名
     * @return 文件内容
     */
    @Tool(description = "Read content from a file in the tmp directory")
    public String readFile(
            @ToolParam(description = "Name of the file to read") String fileName) {
        try {
            // 参数校验
            if (fileName == null || fileName.isEmpty()) {
                return "Error reading file: File name is null or empty.";
            }

            // 构建完整文件路径
            String filePath = BASE_DIR + File.separator + fileName;
            
            // 检查文件是否存在
            if (!FileUtil.exist(filePath)) {
                return "Error reading file: File does not exist: " + fileName;
            }

            // 检查是否为文件（而不是目录）
            File file = new File(filePath);
            if (!file.isFile()) {
                return "Error reading file: Path is not a file: " + fileName;
            }

            // 读取文件内容
            String content = FileUtil.readString(filePath, CharsetUtil.CHARSET_UTF_8);
            log.info("Successfully read file: {}", fileName);
            return content;

        } catch (Exception e) {
            log.error("Error reading file: {}", fileName, e);
            return "Error reading file: " + e.getMessage();
        }
    }

    /**
     * 写入内容到文件
     *
     * @param fileName 文件名
     * @param content  要写入的内容
     * @param append   是否追加模式，默认为false（覆盖模式）
     * @return 操作结果
     */
    @Tool(description = "Write content to a file in the tmp directory")
    public String writeFile(
            @ToolParam(description = "Name of the file to write") String fileName,
            @ToolParam(description = "Content to write to the file") String content) {
        try {
            // 参数校验
            if (fileName == null || fileName.isEmpty()) {
                return "Error writing file: File name is null or empty.";
            }

            if (content == null) {
                content = "";
            }

            // 构建完整文件路径
            String filePath = BASE_DIR + File.separator + fileName;
            
            // 创建父目录（如果不存在）
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                FileUtil.mkdir(parentDir.getAbsolutePath());
            }

            // 写入文件
            FileUtil.writeString(content, filePath, CharsetUtil.CHARSET_UTF_8);

            log.info("Successfully wrote to file: {}", fileName);
            return "Successfully wrote to file: " + fileName;

        } catch (Exception e) {
            log.error("Error writing file: {}", fileName, e);
            return "Error writing file: " + e.getMessage();
        }
    }
}
