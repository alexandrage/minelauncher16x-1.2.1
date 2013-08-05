package net.minecraft.launcher.ui.popups.profile;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import net.minecraft.launcher.events.RefreshedVersionsListener;
import net.minecraft.launcher.profile.Profile;
import net.minecraft.launcher.ui.popups.profile.ProfileEditorPopup;
import net.minecraft.launcher.updater.VersionManager;
import net.minecraft.launcher.updater.VersionSyncInfo;
import net.minecraft.launcher.versions.ReleaseType;
import net.minecraft.launcher.versions.Version;

public class ProfileVersionPanel extends JPanel implements RefreshedVersionsListener {

   private final ProfileEditorPopup editor;
   private final JComboBox versionList = new JComboBox();
   private final List<ProfileVersionPanel.ReleaseTypeCheckBox> customVersionTypes = new ArrayList();


   public ProfileVersionPanel(ProfileEditorPopup editor) {
      this.editor = editor;
      this.setLayout(new GridBagLayout());
      this.setBorder(BorderFactory.createTitledBorder("Version Selection"));
      this.createInterface();
      this.addEventHandlers();
      List versions = editor.getLauncher().getVersionManager().getVersions(editor.getProfile().getVersionFilter());
      if(versions.isEmpty()) {
         editor.getLauncher().getVersionManager().addRefreshedVersionsListener(this);
      } else {
         this.populateVersions(versions);
      }

   }

   protected void createInterface() {
      GridBagConstraints constraints = new GridBagConstraints();
      constraints.insets = new Insets(2, 2, 2, 2);
      constraints.anchor = 17;
      constraints.gridy = 0;
      ReleaseType[] arr$ = ReleaseType.values();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         ReleaseType type = arr$[i$];
         if(type.getDescription() != null) {
            ProfileVersionPanel.ReleaseTypeCheckBox checkbox = new ProfileVersionPanel.ReleaseTypeCheckBox(type, null);
            checkbox.setSelected(this.editor.getProfile().getVersionFilter().getTypes().contains(type));
            this.customVersionTypes.add(checkbox);
            constraints.fill = 2;
            constraints.weightx = 1.0D;
            constraints.gridwidth = 0;
            this.add(checkbox, constraints);
            constraints.gridwidth = 1;
            constraints.weightx = 0.0D;
            constraints.fill = 0;
            ++constraints.gridy;
         }
      }

      this.add(new JLabel("Use version:"), constraints);
      constraints.fill = 2;
      constraints.weightx = 1.0D;
      this.add(this.versionList, constraints);
      constraints.weightx = 0.0D;
      constraints.fill = 0;
      ++constraints.gridy;
      this.versionList.setRenderer(new ProfileVersionPanel.VersionListRenderer(null));
   }

   protected void addEventHandlers() {
      this.versionList.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            ProfileVersionPanel.this.updateVersionSelection();
         }
      });
      Iterator i$ = this.customVersionTypes.iterator();

      while(i$.hasNext()) {
         final ProfileVersionPanel.ReleaseTypeCheckBox type = (ProfileVersionPanel.ReleaseTypeCheckBox)i$.next();
         type.addItemListener(new ItemListener() {

            private boolean isUpdating = false;

            public void itemStateChanged(ItemEvent e) {
               if(!this.isUpdating) {
                  if(e.getStateChange() == 1 && type.getType().getPopupWarning() != null) {
                     int result = JOptionPane.showConfirmDialog(ProfileVersionPanel.this.editor.getLauncher().getFrame(), type.getType().getPopupWarning() + "\n\nAre you sure you want to continue?");
                     this.isUpdating = true;
                     if(result == 0) {
                        type.setSelected(true);
                        ProfileVersionPanel.this.updateCustomVersionFilter();
                     } else {
                        type.setSelected(false);
                     }

                     this.isUpdating = false;
                  } else {
                     ProfileVersionPanel.this.updateCustomVersionFilter();
                  }

               }
            }
         });
      }

   }

   private void updateCustomVersionFilter() {
      Profile profile = this.editor.getProfile();
      HashSet newTypes = new HashSet(Profile.DEFAULT_RELEASE_TYPES);
      Iterator i$ = this.customVersionTypes.iterator();

      while(i$.hasNext()) {
         ProfileVersionPanel.ReleaseTypeCheckBox type = (ProfileVersionPanel.ReleaseTypeCheckBox)i$.next();
         if(type.isSelected()) {
            newTypes.add(type.getType());
         } else {
            newTypes.remove(type.getType());
         }
      }

      if(newTypes.equals(Profile.DEFAULT_RELEASE_TYPES)) {
         profile.setAllowedReleaseTypes((Set)null);
      } else {
         profile.setAllowedReleaseTypes(newTypes);
      }

      this.populateVersions(this.editor.getLauncher().getVersionManager().getVersions(this.editor.getProfile().getVersionFilter()));
      this.editor.getLauncher().getVersionManager().removeRefreshedVersionsListener(this);
   }

   private void updateVersionSelection() {
      Object selection = this.versionList.getSelectedItem();
      if(selection instanceof VersionSyncInfo) {
         Version version = ((VersionSyncInfo)selection).getLatestVersion();
         this.editor.getProfile().setLastVersionId(version.getId());
      } else {
         this.editor.getProfile().setLastVersionId((String)null);
      }

   }

   private void populateVersions(List<VersionSyncInfo> versions) {
      String previous = this.editor.getProfile().getLastVersionId();
      VersionSyncInfo selected = null;
      this.versionList.removeAllItems();
      this.versionList.addItem("Use Latest Version");

      VersionSyncInfo version;
      for(Iterator i$ = versions.iterator(); i$.hasNext(); this.versionList.addItem(version)) {
         version = (VersionSyncInfo)i$.next();
         if(version.getLatestVersion().getId().equals(previous)) {
            selected = version;
         }
      }

      if(selected == null && !versions.isEmpty()) {
         this.versionList.setSelectedIndex(0);
      } else {
         this.versionList.setSelectedItem(selected);
      }

   }

   public void onVersionsRefreshed(VersionManager manager) {
      List versions = manager.getVersions(this.editor.getProfile().getVersionFilter());
      this.populateVersions(versions);
      this.editor.getLauncher().getVersionManager().removeRefreshedVersionsListener(this);
   }

   public boolean shouldReceiveEventsInUIThread() {
      return true;
   }

   private static class ReleaseTypeCheckBox extends JCheckBox {

      private final ReleaseType type;


      private ReleaseTypeCheckBox(ReleaseType type) {
         super(type.getDescription());
         this.type = type;
      }

      public ReleaseType getType() {
         return this.type;
      }

      // $FF: synthetic method
      ReleaseTypeCheckBox(ReleaseType x0, Object x1) {
         this(x0);
      }
   }

   private static class VersionListRenderer extends BasicComboBoxRenderer {

      private VersionListRenderer() {}

      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
         if(value instanceof VersionSyncInfo) {
            VersionSyncInfo syncInfo = (VersionSyncInfo)value;
            Version version = syncInfo.getLatestVersion();
            value = String.format("%s %s", new Object[]{version.getType().getName(), version.getId()});
         }

         super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
         return this;
      }

      // $FF: synthetic method
      VersionListRenderer(Object x0) {
         this();
      }
   }
}
