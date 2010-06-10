package lbms.plugins.mldht.kad;

import java.util.*;

import lbms.plugins.mldht.kad.DHT.DHTtype;
import lbms.plugins.mldht.kad.messages.AnnounceRequest;
import lbms.plugins.mldht.kad.messages.GetPeersRequest;
import lbms.plugins.mldht.kad.messages.GetPeersResponse;
import lbms.plugins.mldht.kad.messages.MessageBase;
import lbms.plugins.mldht.kad.messages.MessageBase.Method;

/**
 * @author Damokles
 *
 */
public class AnnounceTask extends Task {

	private int								port;
	private List<KBucketEntryAndToken>		answered;										// nodes which have answered with values
	private List<KBucketEntry>				answered_visited;								// nodes which have answered with values which have been visited

	private Database						db;
	private Set<DBItem>						returned_items;
	private SortedSet<KBucketEntryAndToken>	closestSet;

	private int								validReponsesSinceLastClosestSetModification;

	public AnnounceTask (Database db, RPCServerBase rpc, Node node,
			Key info_hash, int port) {
		super(info_hash, rpc, node);
		answered = new ArrayList<KBucketEntryAndToken>();
		answered_visited = new ArrayList<KBucketEntry>();
		returned_items = new HashSet<DBItem>();
		this.db = db;
		this.port = port;

		this.closestSet = new TreeSet<KBucketEntryAndToken>(new KBucketEntry.DistanceOrder(targetKey));

		DHT.logDebug("AnnounceTask started: " + getTaskID());
	}

	/* (non-Javadoc)
	 * @see lbms.plugins.mldht.kad.Task#callFinished(lbms.plugins.mldht.kad.RPCCall, lbms.plugins.mldht.kad.messages.MessageBase)
	 */
	@Override
	void callFinished (RPCCallBase c, MessageBase rsp) {
		// if we do not have a get peers response, return
		// announce_peer's response are just empty anyway
		if (c.getMessageMethod() != Method.GET_PEERS) {
			return;
		}

		// it is either a GetPeersNodesRsp or a GetPeersValuesRsp
		GetPeersResponse gpr;
		if (rsp instanceof GetPeersResponse) {
			gpr = (GetPeersResponse) rsp;
		} else {
			return;
		}

		
		for (DHTtype type : DHTtype.values())
		{
			byte[] nodes = gpr.getNodes(type);
			if (nodes == null)
				continue;
			int nval = nodes.length / type.NODES_ENTRY_LENGTH;
			if (type == rpc.getDHT().getType())
			{
				synchronized (todo)
				{
					for (int i = 0; i < nval; i++)
					{
						// add node to todo list
						KBucketEntry e = PackUtil.UnpackBucketEntry(nodes, i * type.NODES_ENTRY_LENGTH, type);
						if (!todo.contains(e) && !visited.contains(e) && todo.size() < 100)
						{
							todo.add(e);
						}
					}
				}

			} else
			{
				for (int i = 0; i < nval; i++)
				{
					KBucketEntry e = PackUtil.UnpackBucketEntry(nodes, i * type.NODES_ENTRY_LENGTH, type);
					DHT.getDHT(type).addDHTNode(e.getAddress().getAddress().getHostAddress(), e.getAddress().getPort());
				}
			}
		}

		// store the items in the database
		List<DBItem> items = gpr.getPeerItems();
		for (DBItem item : items)
		{
			db.store(targetKey, item);
			// also add the items to the returned_items list
			returned_items.add(item);
		}

		if (gpr.getToken() != null && !gpr.getPeerItems().isEmpty())
		{
			// add the peer who responded to the answered list, so we can do an announce
			KBucketEntry e = new KBucketEntry(rsp.getOrigin(), rsp.getID());
			KBucketEntryAndToken newEntry = new KBucketEntryAndToken(e, gpr.getToken());
			synchronized (answered)
			{
				synchronized (answered_visited)
				{
					if (!answered.contains(newEntry) && !answered_visited.contains(e))
					{
						answered.add(newEntry);
					}
				}
			}
		}
		
		if (gpr.getToken() != null)
		{
			// add the peer who responded to the closest nodes list, so we can do an announce
			// if no or less then K Nodes respond with peers
			KBucketEntry entry = new KBucketEntry(rsp.getOrigin(), rsp.getID());
			KBucketEntryAndToken toAdd = new KBucketEntryAndToken(entry, gpr.getToken());
			synchronized (closestSet)
			{
				closestSet.add(toAdd);
				if (closestSet.size() > DHTConstants.MAX_ENTRIES_PER_BUCKET)
				{
					KBucketEntryAndToken last = closestSet.last();
					closestSet.remove(last);
					if (toAdd == last)
					{
						validReponsesSinceLastClosestSetModification++;
					} else
					{
						validReponsesSinceLastClosestSetModification = 0;
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see lbms.plugins.mldht.kad.Task#callTimeout(lbms.plugins.mldht.kad.RPCCall)
	 */
	@Override
	void callTimeout (RPCCallBase c) {

	}

	/* (non-Javadoc)
	 * @see lbms.plugins.mldht.kad.Task#update()
	 */
	@Override
	synchronized void update () {
		synchronized (answered) {
			while (!answered.isEmpty() && canDoRequest()) {
				KBucketEntryAndToken e = answered.remove(0);
				synchronized (answered_visited) {
					if (!answered_visited.contains(e)) {
						AnnounceRequest anr = new AnnounceRequest(node.getOurID(), targetKey, port, e.getToken());
						//System.out.println("sending announce to ID:"+e.getID()+" addr:"+e.getAddress());
						anr.setOrigin(e.getAddress());
						rpcCall(anr);
						answered_visited.add(e);
					}
				}
			}
		}

		synchronized (todo) {
			// go over the todo list and send get_peers requests
			// until we have nothing left
			while (!todo.isEmpty() && canDoRequest()) {
				KBucketEntry e = todo.first();
				todo.remove(e);
				// only send a findNode if we haven't allready visited the node
				if (!visited.contains(e)) {
					// send a findNode to the node
					GetPeersRequest gpr = new GetPeersRequest(node.getOurID(),targetKey);
					gpr.setWant4(rpc.getDHT().getType() == DHTtype.IPV4_DHT || DHT.getDHT(DHTtype.IPV4_DHT).getNode().getNumEntriesInRoutingTable() < DHTConstants.BOOTSTRAP_IF_LESS_THAN_X_PEERS);
					gpr.setWant6(rpc.getDHT().getType() == DHTtype.IPV6_DHT || DHT.getDHT(DHTtype.IPV6_DHT).getNode().getNumEntriesInRoutingTable() < DHTConstants.BOOTSTRAP_IF_LESS_THAN_X_PEERS);
					gpr.setDestination(e.getAddress());
					rpcCall(gpr);
					visited.add(e);
				}
			}
		}

		if (todo.isEmpty() && answered.isEmpty()
				&& getNumOutstandingRequests() == 0 && !isFinished()) {
			done();
		} else if (answered_visited.size() >= DHTConstants.MAX_ENTRIES_PER_BUCKET) {
			// if K announces have occurred stop
			done();
		} else if (validReponsesSinceLastClosestSetModification >= DHTConstants.MAX_CONCURRENT_REQUESTS) {
			synchronized (answered_visited)
			{
				synchronized (answered)
				{
					synchronized (closestSet)
					{
						SortedSet<Key> toEstimate = new TreeSet<Key>();
						int remainingAnnounces = DHTConstants.MAX_ENTRIES_PER_BUCKET - answered_visited.size();
						for(KBucketEntryAndToken e : closestSet)
						{
							toEstimate.add(e.getID());
							if (!answered.contains(e) && !answered_visited.contains(e) && remainingAnnounces > 0)
							{
								answered.add(e);
								remainingAnnounces--;
							}
						}
						rpc.getDHT().getEstimator().update(toEstimate);
					}
				}
			}
			validReponsesSinceLastClosestSetModification = 0;
		}
	}


	@Override
	protected void done() {
		super.done();
		if(validReponsesSinceLastClosestSetModification >= DHTConstants.MAX_CONCURRENT_REQUESTS)
			synchronized (closestSet)
			{
				SortedSet<Key> toEstimate = new TreeSet<Key>();
				for(KBucketEntryAndToken e : closestSet)
					toEstimate.add(e.getID());
				rpc.getDHT().getEstimator().update(toEstimate);
			}
		
		//System.out.println(returned_items);
	}

	/**
	 * @return the returned_items
	 */
	public Set<DBItem> getReturnedItems () {
		return Collections.unmodifiableSet(returned_items);
	}

	/**
	 * @return the info_hash
	 */
	public Key getInfoHash () {
		return targetKey;
	}

	/* (non-Javadoc)
	 * @see lbms.plugins.mldht.kad.Task#start()
	 */
	@Override
	void start () {
		//delay the filling of the todo list until we actually start the task
		KClosestNodesSearch kns = new KClosestNodesSearch(targetKey,
				DHTConstants.MAX_ENTRIES_PER_BUCKET * 4,rpc.getDHT());

		kns.fill();

		if (kns.getNumEntries() > 0) {
			todo.addAll(kns.getEntries());
		}

		super.start();
	}
}
