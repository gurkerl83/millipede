package org.millipede.router.vo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProviderVO implements Serializable {

    private String title;
    private String category;
    private boolean enabled;
//	@SuppressWarnings("unchecked")
//	private List<List> category;

    public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

//    /**
//     * @return the type
//     */
//    public String getType() {
//        return type;
//    }
//
//    /**
//     * @param type the type to set
//     */
//    public void setType(String type) {
//        this.type = type;
//    }
//	@SuppressWarnings("unchecked")
//	public void setCategory(List<List> category) {
//		this.category = category;
//	}
//	@SuppressWarnings("unchecked")
//	public List<List> getCategory() {
//		return category;
//	}

    public byte[] getBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oout = null;
        try {
            //		try {
            oout = new ObjectOutputStream(baos);
        } catch (IOException ex) {
            Logger.getLogger(ProviderVO.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            //		}
            //		try {
            oout.writeObject(this);
        } catch (IOException ex) {
            Logger.getLogger(ProviderVO.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            //		}
            //		try {
            oout.close();
        } catch (IOException ex) {
            Logger.getLogger(ProviderVO.class.getName()).log(Level.SEVERE, null, ex);
        }
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        return baos.toByteArray();
    }

    public static ProviderVO getProviderVO(byte[] buf) {
        if (buf != null) {
            ObjectInputStream objectIn = null;
            try {
                //			try {
                objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
                try {
                    //			} catch (IOException e) {
                    //				// TODO Auto-generated catch block
                    //				e.printStackTrace();
                    //			}
                    //			try {
                    return (ProviderVO) objectIn.readObject();
                    //			} catch (IOException e) {
                    //				// TODO Auto-generated catch block
                    //				e.printStackTrace();
                    //			} catch (ClassNotFoundException e) {
                    //				// TODO Auto-generated catch block
                    //			} // Contains the object
                    //			} // Contains the object
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ProviderVO.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                Logger.getLogger(ProviderVO.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return null;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }
}
