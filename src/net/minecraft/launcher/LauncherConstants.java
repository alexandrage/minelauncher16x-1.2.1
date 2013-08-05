package net.minecraft.launcher;

import java.net.URI;
import java.net.URISyntaxException;

public class LauncherConstants {

   public static final String VERSION_NAME = "1.2.1";
   public static final int VERSION_NUMERIC = 7;
   public static final String BASE_URL = "http://webmcr.caver.org/";
   public static final String ROUTE_AUTHENTICATE = BASE_URL + "authenticate";
   public static final String ROUTE_REFRESH = BASE_URL + "refresh";
   public static final String ROUTE_INVALIDATE = BASE_URL + "invalidate";
   public static final String SERVER_DOWNLOAD_URL = BASE_URL + "MineCraft/MinecraftDownload/";
   public static final String SERVER_MODS_URL = BASE_URL + "MineCraft/MinecraftResources/";
   public static final String NEWS_URL = BASE_URL + "news.php";
   public static final URI URL_REGISTER = constantURI(BASE_URL);
   public static final URI URL_FORGOT_USERNAME = constantURI("http://help.mojang.com/customer/portal/articles/1233873");
   public static final URI URL_FORGOT_PASSWORD_MINECRAFT = constantURI(BASE_URL);

/*   public static final String  = BASE_URL + "";
   public static final String  = "";*/


//   public static final String URL_DOWNLOAD_BASE = "https://s3.amazonaws.com/Minecraft.Download/";
//   public static final String URL_RESOURCE_BASE = "https://s3.amazonaws.com/Minecraft.Resources/";
//   public static final String URL_BLOG = "http://mcupdate.tumblr.com";
//   public static final String URL_SUPPORT = "http://help.mojang.com";
//   public static final String URL_STATUS_CHECKER = "http://status.mojang.com/check";
//   public static final int UNVERSIONED_BOOTSTRAP_VERSION = 0;
//   public static final int MINIMUM_BOOTSTRAP_SUPPORTED = 4;
//   public static final String URL_BOOTSTRAP_DOWNLOAD = "https://mojang.com/2013/06/minecraft-1-6-pre-release/";
   public static final String[] BOOTSTRAP_OUT_OF_DATE_BUTTONS = new String[]{"Go to URL", "Close"};
   public static final String[] CONFIRM_PROFILE_DELETION_OPTIONS = new String[]{"Delete profile", "Cancel"};
//   public static final URI URL_FORGOT_MIGRATED_EMAIL = constantURI("http://help.mojang.com/customer/portal/articles/1205055-minecraft-launcher-error---migrated-account");
//   public static final int MAX_NATIVES_LIFE_IN_SECONDS = 3600;
//   public static final String DEFAULT_VERSION_INCOMPATIBILITY_REASON = "This version is incompatible with your computer. Please try another one by going into Edit Profile and selecting one through the dropdown. Sorry!";


   public static URI constantURI(String input) {
      try {
         return new URI(input);
      } catch (URISyntaxException var2) {
         throw new Error(var2);
      }
   }

}
