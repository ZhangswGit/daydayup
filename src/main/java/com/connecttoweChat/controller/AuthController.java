package com.connecttoweChat.controller;

import com.connecttoweChat.Utils.SecurityUtils;
import com.connecttoweChat.bean.Role;
import com.connecttoweChat.constants.AuthConstant;
import com.connecttoweChat.mapper.ResourceMapper;
import com.connecttoweChat.mapper.RoleMapper;
import com.connecttoweChat.mapper.UserMapper;
import com.connecttoweChat.signOn.SignOnChain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/admin/v1")
@Slf4j
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    SignOnChain signOnChainImpl;

    static final int BUFFER_SIZE = 2 * 1024;

    @GetMapping("/auth")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        signOnChainImpl.sign(request, response);
        String username = SecurityUtils.currentUser();
        if (StringUtils.isEmpty(username)) {
            return "";
        }
        return "true";
    }

    @GetMapping("/loginOut")
    public String loginOut(HttpServletRequest request, HttpServletResponse response) {
        SecurityUtils.loginOut(request, response);
        return "true";
    }

    @GetMapping("/code")
    @Secured({AuthConstant.ADMIN})
    public String sendCode(HttpServletRequest request, HttpServletResponse response) {
        String username = SecurityUtils.currentUser();
        resourceMapper.deleteById(4);
        List<Role> allRole = roleMapper.findAllRole();
        Optional<Role> role = roleMapper.findRoleById(1l);
        return username;
    }

    @GetMapping("/pac")
    public void getPac(HttpServletRequest request, HttpServletResponse response) {
        String autoConfigUrl = String.format("%s://%s:%s/pac/%d", request.getScheme(), request.getServerName(), request.getServerPort(), 1);
        String system = request.getHeader("User-Agent");
        response.setCharacterEncoding("GBK");

        if (!system.contains("Windows")) {
            response.setContentType("multipart/form-data;charset=GBK");
            response.setHeader("Content-disposition", "attachment;filename=" + "windows.bat");
            Resource resource = new ClassPathResource("config/resource/windows.tpl");
            try (
                    OutputStream outputStream = response.getOutputStream();
                    InputStream inputStream = resource.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "GBK");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            ) {
                String context = "";
                while ((context = bufferedReader.readLine()) != null) {
                    if (context.contains("$PAC_URL$")) {
                        context = context.replace("$PAC_URL$", autoConfigUrl);
                    }
                    outputStream.write(context.getBytes("GBK"));
                    outputStream.write("\r\n".getBytes());
                }
                outputStream.flush();
            } catch (Exception e) {
                log.error("Export pac filename:{} failed", "windows.bat");
            }
        } else {
            try (
                    OutputStream outputStream = response.getOutputStream();
                    ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
            ) {
                response.setContentType("multipart/form-data;charset=GBK");
                response.setHeader("Content-disposition", "attachment;filename=" + "pac-app.zip");
                URL url = this.getClass().getClassLoader().getResource("config/resource/pac-app.app");
                File file = new File(url.getPath());
                //压缩pac-app.app
                compress(file, zipOutputStream, file.getName());
                //压缩auto-config-url.txt文件
                zipOutputStream.putNextEntry(new ZipEntry("auto-config-url.txt"));
                zipOutputStream.write(autoConfigUrl.getBytes());
                zipOutputStream.closeEntry();
                zipOutputStream.finish();
            } catch (Exception e) {
                log.error("Export pac filename:{} failed", "pac-app.zip");
            }
        }
    }

    private void compress(File sourceFile, ZipOutputStream zos, String name) throws Exception {

        byte[] buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                // 空文件夹的处理
                zos.putNextEntry(new ZipEntry(name + "/"));
                // 没有文件，不需要文件的copy
                zos.closeEntry();
            } else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                    // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                    compress(file, zos, name + "/" + file.getName());
                }
            }
        }
    }
}
