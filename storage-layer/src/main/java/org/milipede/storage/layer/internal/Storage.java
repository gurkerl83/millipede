//package org.milipede.storage.layer.internal;
//
//import java.util.concurrent.Callable;
//
//import javax.swing.JOptionPane;
//
////import org.jdesktop.application.Action;
////import org.jdesktop.application.Task;
////import org.jdesktop.beansbinding.Binding;
//import org.milipede.storage.layer.StorageService;
//
//import com.sun.cloud.api.storage.client.StorageClient;
//import com.sun.cloud.api.storage.models.Resource;
//import com.sun.cloud.api.storage.models.Resources;
//import com.sun.cloud.api.storage.models.Snapshot;
//import com.sun.cloud.api.storage.webdav.WebDavClient;
//
////import com.sun.cloud.demo.storage.console.StorageConsoleView.RefreshStorageTask;
////import com.sun.cloud.demo.storage.console.StorageConsoleView.RefreshVolumeTask;
//
//public class Storage implements StorageService {
//
////	private StorageClient storageClient = null;
//	private WebDavClient webDavClient = null;
//	private Snapshot selectedSnapshot;
//    private Resource selectedVolume;
//    private Resources selectedVolumes;
//
//    private String cloneName = null;
//    private String snapshotName;
//    private String volumeName;
//    private String accessKey = null;
//
//
////    public StorageClient getStorageClient() {
////        return this.storageClient;
////    }
////
////    public void setStorageClient(StorageClient storageClient) {
////        StorageClient old = this.storageClient;
////        this.storageClient = storageClient;
////        firePropertyChange("storageClient", old, storageClient);
////    }
//
//
//    public WebDavClient getWebDavClient() {
//        return this.webDavClient;
//    }
//
//    public void setWebDavClient(WebDavClient webDavClient) {
//        WebDavClient old = this.webDavClient;
//        this.webDavClient = webDavClient;
////        firePropertyChange("webDavClient", old, webDavClient);
//    }
//
//    public String getAccessKey() {
//        return this.accessKey;
//    }
//
//    public void setAccessKey(String accessKey) {
//        String old = this.accessKey;
//        this.accessKey = accessKey;
////        firePropertyChange("accessKey", old, accessKey);
//    }
//
//
//    public String getCloneName() {
//        return this.cloneName;
//    }
//
//    public void setCloneName(String cloneName) {
//        String old = this.cloneName;
//        this.cloneName = cloneName;
////        firePropertyChange("cloneName", old, cloneName);
//    }
//
//    public Snapshot getSelectedSnapshot() {
//        return this.selectedSnapshot;
//    }
//
//    public void setSelectedSnapshot(Snapshot selectedSnapshot) {
//        Snapshot old = this.selectedSnapshot;
//        this.selectedSnapshot = selectedSnapshot;
////        firePropertyChange("selectedSnapshot", old, selectedSnapshot);
//    }
//
//    public Resource getSelectedVolume() {
//        return this.selectedVolume;
//    }
//
//    public void setSelectedVolume(Resource selectedVolume) {
//        Resource old = this.selectedVolume;
//        this.selectedVolume = selectedVolume;
////        rebindSelectedVolume(old, selectedVolume);
////        firePropertyChange("selectedVolume", old, selectedVolume);
//    }
//
//    public Resources getSelectedVolumes() {
//        return this.selectedVolumes;
//    }
//
//    public void setSelectedVolumes(Resources selectedVolumes) {
//        Resources old = this.selectedVolumes;
//        this.selectedVolumes = selectedVolumes;
////        firePropertyChange("selectedVolumes", old, selectedVolumes);
//    }
//
//    public String getSnapshotName() {
//        return this.snapshotName;
//    }
//
//    public void setSnapshotName(String snapshotName) {
//        String old = this.snapshotName;
//        this.snapshotName = snapshotName;
////        firePropertyChange("snapshotName", old, snapshotName);
//    }
//
//    public String getVolumeName() {
//        return this.volumeName;
//    }
//
//    public void setVolumeName(String volumeName) {
//        String old = this.volumeName;
//        this.volumeName = volumeName;
////        firePropertyChange("volumeName", old, volumeName);
//    }
//
////    @Action
//    public void deleteSnapshotAction() {
////        String name = getSelectedSnapshot().getName();
////        int result = JOptionPane.showConfirmDialog(null,
////                                                   "Really delete snapshot " + name + "?");
////        if (result == JOptionPane.YES_OPTION) {
////            startDeleteSnapshotTask(getSelectedVolume().getName(), name);
////        }
//    }
//
////    @Action
//    public void deleteVolumeAction() {
////        int result = JOptionPane.showConfirmDialog(null,
////                                                   "Really delete volume " + getSelectedVolume().getName() + "?");
////        if (result == JOptionPane.YES_OPTION) {
////            startDeleteVolumeTask(getSelectedVolume().getName());
////        }
//    }
//
//
//
////	private class CreateSnapshotTask implements Callable<Resource> {
////
////		private String volumeName;
////		private String snapshotName;
////
////		CreateSnapshotTask(String volumeName, String snapshotName) {
////			this.volumeName = volumeName;
////			this.snapshotName = snapshotName;
////		}
////
////		public Resource call() throws Exception {
////			getStorageClient().createSnapshot(null, volumeName, snapshotName);
////			Resource resource = getStorageClient().findVolume(getAccessKey(),
////					volumeName);
////			return resource; // return your result
////		}
////	}
////
////	private class CreateVolumeTask implements Callable<Resources> {
////
////		private String volumeName;
////
////		CreateVolumeTask(String volumeName) {
////			this.volumeName = volumeName;
////		}
////
////		public Resources call() throws Exception {
////			// TODO Auto-generated method stub
////			getStorageClient().createVolume(null, volumeName);
////			// Update the current volumes information
////			Resources resources = getStorageClient().findVolumes(
////					getAccessKey(), null);
////			return resources; // return your result
////		}
////
////	}
////
////	private class DeleteSnapshotTask implements Callable<Resource> {
////
////		private String volumeName;
////		private String snapshotName;
////
////		DeleteSnapshotTask(String volumeName, String snapshotName) {
////			this.volumeName = volumeName;
////			this.snapshotName = snapshotName;
////		}
////
////		public Resource call() throws Exception {
////			getStorageClient().deleteSnapshot(null, volumeName, snapshotName);
////			Resource resource = getStorageClient().findVolume(getAccessKey(),
////					volumeName);
////			return resource; // return your result
////		}
////	}
////
////	private class DeleteVolumeTask implements Callable<Resources> {
////
////		private String volumeName;
////
////		DeleteVolumeTask(String volumeName) {
////			this.volumeName = volumeName;
////		}
////
////		public Resources call() throws Exception {
////			getStorageClient().deleteVolume(null, volumeName);
////			Resources resources = getStorageClient().findVolumes(
////					getAccessKey(), null);
////			return resources; // return your result
////		}
////	}
////
//////	private void rebindSelectedVolume(Resource oldVolume, Resource newVolume) {
//////        for (Binding binding : bindingGroup.getBindings()) {
//////            Object oldSource = binding.getSourceObject();
//////            if (!(oldVolume instanceof Resource)) {
//////                continue;
//////            }
//////            if (oldVolume == (Resource) oldSource) {
////////                System.out.println("Rebinding " + binding.getSourceProperty() + " -> " + binding.getTargetProperty());
//////                binding.unbind();
//////                binding.setSourceObject(newVolume);
//////                binding.bind();
//////            }
//////        }
//////    }
////
//////    @Action
//////    public Task refreshStorageAction() {
//////        if (storageClient == null) {
//////            showCredentialsView();
//////            // Setting credentials will trigger a refresh task, so just return
//////            return null;
//////        } else {
//////            return new RefreshStorageTask(getApplication());
//////        }
//////    }
////
////	private class RefreshStorageTask implements Callable<Resources> {
////
////		RefreshStorageTask() {
////		}
////
////		public Resources call() throws Exception {
////			/*
////			 * // FIXME - work around missing findVolumes() support for now
////			 * Resources resources = new Resources(); Resource resource; for
////			 * (int i = 0; i < NAMES.length; i++) { try {
////			 * System.out.println("Finding volume " + NAMES[i]); resource =
////			 * getStorageClient().findVolume(NAMES[i]);
////			 * resources.getList().add(resource); } catch (NotFoundException e)
////			 * { // Ignore missing volumes } // FIXME - ultimately we will want
////			 * to deal with 401 specifically }
////			 */
////
////			Resources resources = getStorageClient().findVolumes(
////					getAccessKey(), null);
////
////			System.out.println("Total volumes found = "
////					+ resources.getList().size());
////			return resources; // return your result
////		}
////	}
////
//////	@Action
//////    public Task refreshVolumeAction() {
//////        return new RefreshVolumeTask(getApplication());
//////    }
////
////	private class RefreshVolumeTask implements Callable<Resource> {
////
////        private Resource original = null;
////
////        RefreshVolumeTask() {
////            this.original = getSelectedVolume();
////        }
////
////        public Resource call() throws Exception {
////        	Resource resource = getStorageClient().findVolume(getAccessKey(), original.getName());
////            return resource;  // return your result
////        }
////    }
//
//}
