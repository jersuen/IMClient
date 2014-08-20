package com.jersuen.im.service;

import android.os.RemoteException;
import android.text.TextUtils;
import com.jersuen.im.IMService;
import com.jersuen.im.service.aidl.IXmppBinder;
import com.jersuen.im.service.aidl.IXmppManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.w3c.dom.Text;

/**
 * @author JerSuen
 */
public class XmppBinder extends IXmppBinder.Stub{

    private IMService service;

    public XmppBinder(IMService service) {
        this.service = service;
    }

    public XmppManager createConnection() throws RemoteException {
        return service.createConnection();
    }

    public boolean setRosterEntryName(String jid, String name) throws RemoteException {
        return service.setRosterEntryName(jid, name);
    }
    public boolean setVCard(byte[] avatarBytes, String nickName) throws RemoteException {
        return service.setVCard(avatarBytes,nickName);
    }

    public String getNickName(String jid) throws RemoteException {
        if (!TextUtils.isEmpty(jid)) {
            VCard vCard = new VCard();
            try {
                vCard.load(service.createConnection().getConnection(), jid);
                return vCard.getNickName();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
