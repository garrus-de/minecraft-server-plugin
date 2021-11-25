package de.garrus.maven.minecraftserverplugin.mojo;


import de.garrus.maven.minecraftserverplugin.ServerType;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "server-start")
public class StartMojo extends AbstractMojo {


    @Parameter(name = "serverType")
    private ServerType type = ServerType.PAPER;

    @Parameter(name = "serverFolder")
    private File serverFolder = new File("target/mc_server/");

    @Parameter(name = "serverVersion")
    private String serverVersion = "1.15.2";

    @Parameter(name = "jarName", defaultValue = "${project.build.finalName}")
    private String jarName;

    @Parameter(name = "targetFolder", readonly = true, defaultValue = "${project.build.directory}")
    private File targetFolder;

    @Parameter(name = "skipPluginCopy", defaultValue = "false")
    private boolean skipPluginCopy;

    @Parameter(name = "gui", defaultValue = "false")
    private boolean gui;

    private File serverFile;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        serverFile = new File(serverFolder, "server.jar");

        if (serverFile.exists()) {

            if (!skipPluginCopy) {
                copyPluginToServer();
            }

            startServer();
        } else {
            throw new MojoFailureException("Server is not installed.");
        }
    }

    /**
     * copy the plugin jar from the target folder to the plugin folder from the server
     */
    private void copyPluginToServer() throws MojoFailureException {
        File pluginJar = new File(targetFolder, jarName + ".jar");
        if (pluginJar.exists()) {
            try {
                FileUtils.copyFile(pluginJar, new File(serverFolder, "plugins/plugin.jar"));
            } catch (IOException e) {
                getLog().error("Can't copy the plugin File.", e);
            }
        } else {
            throw new MojoFailureException("Can't copy the Plugin Jar.");
        }
    }

    /**
     * start the Spigot/Paper/Bukkit server as {@link Process} form maven
     */
    private void startServer() {
        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("-jar");
        command.add("server.jar");
        if (!gui) {
            command.add("nogui");
        }
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(serverFolder);

            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

            processBuilder.start().waitFor();
        } catch (IOException | InterruptedException e) {
            getLog().error("Can't start the server", e);
        }
    }
}
