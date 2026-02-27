package cn.iocoder.yudao.module.infra.framework.file.core.client.local;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.infra.framework.file.core.client.AbstractFileClient;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地文件客户端
 *
 * @author 芋道源码
 */
@Slf4j
public class LocalFileClient extends AbstractFileClient<LocalFileClientConfig> {

    public LocalFileClient(Long id, LocalFileClientConfig config) {
        super(id, config);
    }

    @Override
    protected void doInit() {
    }

    @Override
    public String upload(byte[] content, String path, String type) {
        // 执行写入
        String filePath = getFilePath(path);
        FileUtil.writeBytes(content, filePath);
        // 拼接返回路径
        return super.formatFileUrl(config.getDomain(), path);
    }

    @Override
    public void delete(String path) {
        String filePath = getFilePath(path);
        FileUtil.del(filePath);
    }

    @Override
    public byte[] getContent(String path) {
        String filePath = getFilePath(path);
        try {
            return FileUtil.readBytes(filePath);
        } catch (IORuntimeException ex) {
            if (ex.getMessage().startsWith("File not exist:")) {
                log.warn("[getContent][path({}) 文件不存在，完整路径={}]", path, filePath);
                return null;
            }
            throw ex;
        }
    }

    /**
     * 拼接物理路径，兼容 Linux/Windows，path 中的 / 会正确解析
     */
    private String getFilePath(String path) {
        if (StrUtil.isEmpty(path)) {
            return config.getBasePath();
        }
        Path base = Paths.get(config.getBasePath());
        Path resolved = base.resolve(path.replace("\\", "/"));
        return resolved.normalize().toString();
    }

}
