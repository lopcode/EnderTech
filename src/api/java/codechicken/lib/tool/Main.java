package lib.tool;

public class Main
{
    public static void main(String[] args) {
        LibDownloader.load();
        try {
            Class<?> c_toolMain = new StripClassLoader().loadClass("ToolMain");
            c_toolMain.getDeclaredMethod("main", String[].class).invoke(null, new Object[]{args});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
