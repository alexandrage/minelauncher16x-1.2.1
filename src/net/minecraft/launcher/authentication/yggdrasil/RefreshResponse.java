package net.minecraft.launcher.authentication.yggdrasil;

import net.minecraft.launcher.authentication.GameProfile;
import net.minecraft.launcher.authentication.yggdrasil.Response;

public class RefreshResponse extends Response {

   private String accessToken;
   private String clientToken;
   private GameProfile selectedProfile;
   private GameProfile[] availableProfiles;


   public String getAccessToken() {
      return this.accessToken;
   }

   public String getClientToken() {
      return this.clientToken;
   }

   public GameProfile[] getAvailableProfiles() {
      return this.availableProfiles;
   }

   public GameProfile getSelectedProfile() {
      return this.selectedProfile;
   }
}
