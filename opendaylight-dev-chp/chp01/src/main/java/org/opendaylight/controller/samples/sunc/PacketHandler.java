package org.opendaylight.controller.samples.sunc;
 
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
 
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.packet.Ethernet;
import org.opendaylight.controller.sal.packet.IDataPacketService;
import org.opendaylight.controller.sal.packet.IListenDataPacket;
import org.opendaylight.controller.sal.packet.IPv4;
import org.opendaylight.controller.sal.packet.Packet;
import org.opendaylight.controller.sal.packet.PacketResult;
import org.opendaylight.controller.sal.packet.RawPacket;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.switchmanager.ISwitchManager;
import org.opendaylight.controller.sal.flowprogrammer.Flow;
import org.opendaylight.controller.sal.flowprogrammer.IFlowProgrammerService;
import org.opendaylight.controller.sal.match.Match;
import org.opendaylight.controller.sal.match.MatchType;
import org.opendaylight.controller.sal.action.Action;
import org.opendaylight.controller.sal.action.Output;
import org.opendaylight.controller.sal.action.SetDlDst;
import org.opendaylight.controller.sal.action.SetDlSrc;
import org.opendaylight.controller.sal.action.SetNwDst;
import org.opendaylight.controller.sal.action.SetNwSrc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileOutputStream;
import java.io.File; 
public class PacketHandler implements IListenDataPacket {
 
 
private static final Logger log = LoggerFactory.getLogger(PacketHandler.class);
	private static final byte[] SET_MAC = {0,0,0,0,0,0x08};
    private IDataPacketService dataPacketService;
    private IFlowProgrammerService flowProgrammerService;
    private ISwitchManager switchManager;
    static private InetAddress intToInetAddress(int i) {
    byte b[] = new byte[] { (byte) ((i>>24)&0xff), (byte) ((i>>16)&0xff), (byte) ((i>>8)&0xff), (byte) (i&0xff) };
    InetAddress addr;
    try {
            addr = InetAddress.getByAddress(b);
        } catch (UnknownHostException e) {
            return null;
        }
 
    return addr;
    }
 
    /*
     * Sets a reference to the requested DataPacketService
     * See Activator.configureInstance(...):
     * c.add(createContainerServiceDependency(containerName).setService(
     * IDataPacketService.class).setCallbacks(
     * "setDataPacketService", "unsetDataPacketService")
     * .setRequired(true));
     */
    void setDataPacketService(IDataPacketService s) {
        log.trace("Set DataPacketService.");
 
        dataPacketService = s;
     }
 
    /*
     * Unsets DataPacketService
     * See Activator.configureInstance(...):
     * c.add(createContainerServiceDependency(containerName).setService(
     * IDataPacketService.class).setCallbacks(
     * "setDataPacketService", "unsetDataPacketService")
     * .setRequired(true));
     */
    void unsetDataPacketService(IDataPacketService s) {
        log.trace("Removed DataPacketService.");
 
        if (dataPacketService == s) {
            dataPacketService = null;
            }
        }
    /*
     * Sets a reference to the requested FlowProgrammerService
     */
    void setFlowProgrammerService(IFlowProgrammerService s) {
    log.info("Set FlowProgrammerService.");
    System.out.println("Set FlowProgrammerService.");
 
        flowProgrammerService = s;
        }
 
    /*
     * Unsets FlowProgrammerService
     */
    void unsetFlowProgrammerService(IFlowProgrammerService s) {
        log.info("Removed FlowProgrammerService.");
        System.out.println("Removed FlowProgrammerService.");
 
        if (flowProgrammerService == s) {
            flowProgrammerService = null;
            }
        }
 
    /*
     * Sets a reference to the requested SwitchManagerService
     */
    void setSwitchManagerService(ISwitchManager s) {
    log.info("Set SwitchManagerService.");
    System.out.println("Set SwitchManagerService.");
 
        switchManager = s;
     }
 
    /*
     * Unsets SwitchManagerService
     */
    void unsetSwitchManagerService(ISwitchManager s) {
    log.info("Removed SwitchManagerService.");
    System.out.println("Removed SwitchManagerService.");
 
    if (switchManager == s) {
        switchManager = null;
            }
        }
 
 
    @Override
    public PacketResult receiveDataPacket(RawPacket inPkt) {
        log.trace("Received data packet.");
 
        //这个connector是数据包来自的交换机
        NodeConnector ingressConnector = inPkt.getIncomingNodeConnector();
        Node node = ingressConnector.getNode();
 
        // 利用DataPacketService解析数据包.
        Packet l2pkt = dataPacketService.decodeDataPacket(inPkt);
 
        if (l2pkt instanceof Ethernet) {
            Object l3Pkt = l2pkt.getPayload();
        if (l3Pkt instanceof IPv4) {
                IPv4 ipv4Pkt = (IPv4) l3Pkt;
    InetAddress destaddr = intToInetAddress(ipv4Pkt.getDestinationAddress());
    InetAddress srcaddr = intToInetAddress(ipv4Pkt.getSourceAddress());
    try{
                    FileOutputStream out = new FileOutputStream(new File("/home/openflow/text.txt"));   
 
                    String s = "Packet to " + destaddr.toString() + " from "+ srcaddr.toString() +" received by node " + node.getNodeIDString() + " on connector " + ingressConnector.getNodeConnectorIDString();
                    out.write(s.getBytes()); 
                    out.close();
                }
    catch (Exception e) {   
            e.printStackTrace();   
                }
                Match match = new Match();
                match.setField(MatchType.DL_TYPE, (short) 0x0800);  // IPv4 ethertype
                match.setField(MatchType.NW_SRC, srcaddr);
                match.setField(MatchType.NW_DST, destaddr);
                //设计对packet使用的actions
                List<Action> actions = new LinkedList<Action>();
                //设置新的目的IP
                actions.add(new SetNwDst(destaddr));
                //设置新的目的MAC
                actions.add(new SetDlDst(SET_MAC));
                //创建流
                Flow flow = new Flow(match, actions);
                Status status = flowProgrammerService.addFlow(node, flow);
    if (!status.isSuccess()) {
log.error("Could not program flow: " + status.getDescription());
                }
			if(!status.isSuccess())
				System.out.println("this flow seting failed! ");
            }
			return PacketResult.KEEP_PROCESSING;
        }
    return PacketResult.IGNORED;
    }
}