package de.garrus.maven.minecraftserverplugin.mojo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.garrus.maven.minecraftserverplugin.ServerType;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.net.URL;

@Mojo(name = "server-install", requiresOnline = true)
public class InstallMojo extends AbstractMojo {
    private static final String PAPER_API = "https://papermc.io/api/v1/paper/";

    /**
     * The Server type (PAPER,SPIGOT,BUKKIT)
     */
    @Parameter(name = "serverType")
    private ServerType type = ServerType.PAPER;

    /**
     * the working folder from the server
     */
    @Parameter(name = "serverFolder")
    private File serverFolder = new File("target/mc_server/");

    @Parameter(name = "serverVersion")
    private String serverVersion = "1.15.2";


    /**
     * Create the eula.txt an set accept eula to true
     */
    @Parameter(name = "createEula")
    private boolean createEula = false;

    /**
     * delete the server jar and downloaded new from the server
     */
    @Parameter(name = "overrideServerJar")
    private boolean overrideServerJar = false;

    /**
     * delete the server folder
     */
    @Parameter(name = "reinstallServer")
    private boolean reinstallServer = false;

    /**
     * delete all world from the server
     */
    @Parameter(name = "resetWorld")
    private boolean resetWorld = false;

    /**
     * delete all Plugin and Plugin Config in the plugin Folder
     */
    @Parameter(name = "cleanPluginFolder")
    private boolean cleanPluginFolder = false;


    private File serverFile;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        serverFile = new File(serverFolder, "server.jar");

        if (reinstallServer) {
            cleanServerFolder();
        }

        if (resetWorld) {
            resetWorld();
        }

        if (cleanPluginFolder) {
            cleanPluginFolder();
        }

        if (!serverFile.exists() || overrideServerJar) {
            serverFolder.mkdirs();
            downloadServer();

            if (createEula) {
                createEula();
            }

        } else {
            getLog().info("Server already installed. Skip Installer.");
        }
    }


    /**
     * delete the plugin folder
     */
    private void cleanPluginFolder() {
        try {
            FileUtils.deleteDirectory(new File(serverFolder, "plugins/"));
        } catch (IOException e) {
            getLog().error("Can't delete the plugin folder", e);
        }
    }

    private void cleanServerFolder() {
        try {
            FileUtils.deleteDirectory(serverFolder);
        } catch (IOException e) {
            getLog().error("Can't delete the server folder", e);
        }
        serverFolder.mkdirs();
    }

    /**
     * delete the world Folders
     */
    private void resetWorld() {
        try {
            FileUtils.deleteDirectory(new File(serverFolder, "world"));
            FileUtils.deleteDirectory(new File(serverFolder, "world_the_end"));
            FileUtils.deleteDirectory(new File(serverFolder, "world_nether"));
        } catch (IOException e) {
            getLog().error("Can't delete the world Folder", e);
        }
    }

    /**
     * create and accept the eula.txt file
     */
    private void createEula() {
        File eulaFile = new File(serverFolder, "eula.txt");

        if (!eulaFile.exists()) {
            try {
                FileWriter eulaWriter = new FileWriter(eulaFile);
                eulaWriter.write("eula=true");

                eulaWriter.flush();
                eulaWriter.close();
            } catch (IOException e) {
                getLog().error("Can't write the Server Eula File", e);
            }
        }
    }

    private void downloadServer() {
        switch (type) {
            case PAPER:
                downloadServerJar(getPaperServerDownload());
                break;
            case BUKKIT:
            case SPIGOT:
            default:
                throw new UnsupportedOperationException("Server Type current not supported.");
        }
    }

    /**
     * download the jar file from the url
     */
    private void downloadServerJar(URL downloadUrl) {
        try {
            InputStream serverJarStream = downloadUrl.openStream();

            FileUtils.copyInputStreamToFile(serverJarStream, serverFile);
        } catch (IOException e) {
            getLog().error("Can't load the Server Jar form the Server.", e);
        }
    }

    /**
     * get the download url from the papermc.io api server
     *
     * @return the downlaod Url from the papermc Server Jar
     */
    public URL getPaperServerDownload() {
        try {
            URL paperApiURL = new URL(PAPER_API + serverVersion);
            JsonObject versionJson = new Gson().fromJson(new InputStreamReader(paperApiURL.openStream()), JsonObject.class);

            if (versionJson.has("builds")) {
                String buildVersion = versionJson.get("builds").getAsJsonObject().get("latest").getAsString();

                return new URL(PAPER_API + serverVersion + "/" + buildVersion + "/download");
            } else {
                getLog().error("Can't find the build version in the api json");
                return null;
            }
        } catch (IOException e) {
            getLog().error("Can't connect to the Papermc.io Server.", e);
            return null;
        }
    }
}
