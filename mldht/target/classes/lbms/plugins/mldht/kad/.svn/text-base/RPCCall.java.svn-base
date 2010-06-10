package lbms.plugins.mldht.kad;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import lbms.plugins.mldht.kad.messages.MessageBase;
import lbms.plugins.mldht.kad.messages.MessageBase.Method;

/**
 * @author Damokles
 *
 */
public class RPCCall implements RPCCallBase {

	private MessageBase				msg;
	private RPCServerBase			rpc;
	private boolean					queued = true;
	private List<RPCCallListener>	listener;
	private ScheduledFuture<?>		timeoutTimer;

	public RPCCall (RPCServerBase rpc, MessageBase msg) {
		this.rpc = rpc;
		this.msg = msg;
	}

	/* (non-Javadoc)
	 * @see lbms.plugins.mldht.kad.RPCCallBase#start()
	 */
	public void start () {
		queued = false;
		startTimeout();
	}

	/* (non-Javadoc)
	 * @see lbms.plugins.mldht.kad.RPCCallBase#response(lbms.plugins.mldht.kad.messages.MessageBase)
	 */
	public void response (MessageBase rsp) {
		if (timeoutTimer != null) {
			timeoutTimer.cancel(false);
		}
		onCallResponse(rsp);
	}

	/* (non-Javadoc)
	 * @see lbms.plugins.mldht.kad.RPCCallBase#addListener(lbms.plugins.mldht.kad.RPCCallListener)
	 */
	public void addListener (RPCCallListener cl) {
		if (listener == null) {
			listener = new ArrayList<RPCCallListener>(1);
		}
		listener.add(cl);
	}

	/* (non-Javadoc)
	 * @see lbms.plugins.mldht.kad.RPCCallBase#removeListener(lbms.plugins.mldht.kad.RPCCallListener)
	 */
	public void removeListener (RPCCallListener cl) {
		if (listener != null) {
			listener.remove(cl);
		}
	}

	/* (non-Javadoc)
	 * @see lbms.plugins.mldht.kad.RPCCallBase#getMessageMethod()
	 */
	public Method getMessageMethod () {
		if (msg != null) {
			return msg.getMethod();
		} else {
			return Method.NONE;
		}
	}

	/// Get the request sent
	/* (non-Javadoc)
	 * @see lbms.plugins.mldht.kad.RPCCallBase#getRequest()
	 */
	public MessageBase getRequest () {
		return msg;
	}

	/* (non-Javadoc)
	 * @see lbms.plugins.mldht.kad.RPCCallBase#isQueued()
	 */
	public boolean isQueued () {
		return queued;
	}

	private void startTimeout () {
		timeoutTimer = DHT.getScheduler().schedule(new Runnable() {
			/* (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			public void run () {
				onCallTimeout();
			}
		}, DHTConstants.RPC_CALL_TIMEOUT, TimeUnit.MILLISECONDS);
	}

	private void onCallResponse (MessageBase rsp) {
		if (listener != null) {
			for (int i = 0; i < listener.size(); i++) {
				listener.get(i).onResponse(this, rsp);
			}
		}
	}

	private void onCallTimeout () {
		if (msg != null) {
			DHT.logDebug("RPCCall timed out ID: " + new String(msg.getMTID()));
			try {
				rpc.timedOut(msg.getMTID());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (listener != null) {
			for (int i = 0; i < listener.size(); i++) {
				try {
					listener.get(i).onTimeout(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
