package net.minecraft.launcher.ui.tabs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.AbstractTableModel;
import net.minecraft.launcher.Launcher;
import net.minecraft.launcher.LauncherConstants;
import net.minecraft.launcher.authentication.AuthenticationDatabase;
import net.minecraft.launcher.authentication.AuthenticationService;
import net.minecraft.launcher.events.RefreshedProfilesListener;
import net.minecraft.launcher.profile.Profile;
import net.minecraft.launcher.profile.ProfileManager;
import net.minecraft.launcher.ui.popups.profile.ProfileEditorPopup;

public class ProfileListTab extends JScrollPane implements RefreshedProfilesListener {

   private static final int COLUMN_NAME = 0;
   private static final int COLUMN_VERSION = 1;
   private static final int COLUMN_AUTHENTICATION = 2;
   private static final int NUM_COLUMNS = 3;
   private final Launcher launcher;
   private final ProfileListTab.ProfileTableModel dataModel = new ProfileListTab.ProfileTableModel(null);
   private final JTable table;
   private final JPopupMenu popupMenu;
   private final JMenuItem addProfileButton;
   private final JMenuItem copyProfileButton;
   private final JMenuItem deleteProfileButton;


   public ProfileListTab(Launcher launcher) {
      this.table = new JTable(this.dataModel);
      this.popupMenu = new JPopupMenu();
      this.addProfileButton = new JMenuItem("Add Profile");
      this.copyProfileButton = new JMenuItem("Copy Profile");
      this.deleteProfileButton = new JMenuItem("Delete Profile");
      this.launcher = launcher;
      this.setViewportView(this.table);
      this.createInterface();
      launcher.getProfileManager().addRefreshedProfilesListener(this);
   }

   protected void createInterface() {
      this.popupMenu.add(this.addProfileButton);
      this.popupMenu.add(this.copyProfileButton);
      this.popupMenu.add(this.deleteProfileButton);
      this.table.setFillsViewportHeight(true);
      this.table.setSelectionMode(0);
      this.popupMenu.addPopupMenuListener(new PopupMenuListener() {
         public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            int[] selection = ProfileListTab.this.table.getSelectedRows();
            boolean hasSelection = selection != null && selection.length > 0;
            ProfileListTab.this.copyProfileButton.setEnabled(hasSelection);
            ProfileListTab.this.deleteProfileButton.setEnabled(hasSelection);
         }
         public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
         public void popupMenuCanceled(PopupMenuEvent e) {}
      });
      this.addProfileButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Profile profile = new Profile();
            profile.setName("New Profile");

            while(ProfileListTab.this.launcher.getProfileManager().getProfiles().containsKey(profile.getName())) {
               profile.setName(profile.getName() + "_");
            }

            ProfileEditorPopup.showEditProfileDialog(ProfileListTab.this.getLauncher(), profile);
         }
      });
      this.copyProfileButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            int selection = ProfileListTab.this.table.getSelectedRow();
            if(selection >= 0 && selection < ProfileListTab.this.table.getRowCount()) {
               Profile current = (Profile)ProfileListTab.this.dataModel.profiles.get(selection);
               Profile copy = new Profile(current);
               copy.setName("Copy of " + current.getName());

               while(ProfileListTab.this.launcher.getProfileManager().getProfiles().containsKey(copy.getName())) {
                  copy.setName(copy.getName() + "_");
               }

               ProfileEditorPopup.showEditProfileDialog(ProfileListTab.this.getLauncher(), copy);
            }
         }
      });
      this.deleteProfileButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            int selection = ProfileListTab.this.table.getSelectedRow();
            if(selection >= 0 && selection < ProfileListTab.this.table.getRowCount()) {
               Profile current = (Profile)ProfileListTab.this.dataModel.profiles.get(selection);
               int result = JOptionPane.showOptionDialog(ProfileListTab.this.launcher.getFrame(), "Are you sure you want to delete this profile?", "Profile Confirmation", 0, 2, (Icon)null, LauncherConstants.CONFIRM_PROFILE_DELETION_OPTIONS, LauncherConstants.CONFIRM_PROFILE_DELETION_OPTIONS[0]);
               if(result == 0) {
                  ProfileListTab.this.launcher.getProfileManager().getProfiles().remove(current.getName());

                  try {
                     ProfileListTab.this.launcher.getProfileManager().saveProfiles();
                     ProfileListTab.this.launcher.getProfileManager().fireRefreshEvent();
                  } catch (IOException var6) {
                     ProfileListTab.this.launcher.println("Couldn\'t save profiles whilst deleting \'" + current.getName() + "\'", var6);
                  }
               }

            }
         }
      });
      this.table.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            if(e.getClickCount() == 2) {
               int row = ProfileListTab.this.table.getSelectedRow();
               if(row >= 0 && row < ProfileListTab.this.dataModel.profiles.size()) {
                  ProfileEditorPopup.showEditProfileDialog(ProfileListTab.this.getLauncher(), (Profile)ProfileListTab.this.dataModel.profiles.get(row));
               }
            }

         }
         public void mouseReleased(MouseEvent e) {
            if(e.isPopupTrigger() && e.getComponent() instanceof JTable) {
               int r = ProfileListTab.this.table.rowAtPoint(e.getPoint());
               if(r >= 0 && r < ProfileListTab.this.table.getRowCount()) {
                  ProfileListTab.this.table.setRowSelectionInterval(r, r);
               } else {
                  ProfileListTab.this.table.clearSelection();
               }

               ProfileListTab.this.popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }

         }
         public void mousePressed(MouseEvent e) {
            if(e.isPopupTrigger() && e.getComponent() instanceof JTable) {
               int r = ProfileListTab.this.table.rowAtPoint(e.getPoint());
               if(r >= 0 && r < ProfileListTab.this.table.getRowCount()) {
                  ProfileListTab.this.table.setRowSelectionInterval(r, r);
               } else {
                  ProfileListTab.this.table.clearSelection();
               }

               ProfileListTab.this.popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }

         }
      });
   }

   public Launcher getLauncher() {
      return this.launcher;
   }

   public void onProfilesRefreshed(ProfileManager manager) {
      this.dataModel.setProfiles(manager.getProfiles().values());
   }

   public boolean shouldReceiveEventsInUIThread() {
      return true;
   }

   private class ProfileTableModel extends AbstractTableModel {

      private final List<Profile> profiles;


      private ProfileTableModel() {
         this.profiles = new ArrayList();
      }

      public int getRowCount() {
         return this.profiles.size();
      }

      public int getColumnCount() {
         return 3;
      }

      public Class<?> getColumnClass(int columnIndex) {
         return String.class;
      }

      public String getColumnName(int column) {
         switch(column) {
         case 0:
            return "Version name";
         case 1:
            return "Version";
         case 2:
            return "Username";
         default:
            return super.getColumnName(column);
         }
      }

      public Object getValueAt(int rowIndex, int columnIndex) {
         Profile profile = (Profile)this.profiles.get(rowIndex);
         AuthenticationDatabase authDatabase = ProfileListTab.this.launcher.getProfileManager().getAuthDatabase();
         AuthenticationService auth = authDatabase.getByUUID(profile.getPlayerUUID());
         switch(columnIndex) {
         case 0:
            return profile.getName();
         case 1:
            if(profile.getLastVersionId() == null) {
               return "(Latest version)";
            }

            return profile.getLastVersionId();
         case 2:
            if(auth != null && auth.getSelectedProfile() != null) {
               return auth.getSelectedProfile().getName();
            }

            return "(Not logged in)";
         default:
            return null;
         }
      }

      public void setProfiles(Collection<Profile> profiles) {
         this.profiles.clear();
         this.profiles.addAll(profiles);
         this.fireTableDataChanged();
      }

      // $FF: synthetic method
      ProfileTableModel(Object x1) {
         this();
      }
   }
}
