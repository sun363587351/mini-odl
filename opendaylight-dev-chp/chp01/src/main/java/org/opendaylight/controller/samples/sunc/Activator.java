/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.opendaylight.controller.samples.sunc;

import java.util.Dictionary;
import java.util.Hashtable;
 
import org.apache.felix.dm.Component;
import org.opendaylight.controller.sal.core.ComponentActivatorAbstractBase;
import org.opendaylight.controller.sal.flowprogrammer.IFlowProgrammerService;
import org.opendaylight.controller.sal.packet.IDataPacketService;
import org.opendaylight.controller.sal.packet.IListenDataPacket;
import org.opendaylight.controller.switchmanager.ISwitchManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
public class Activator extends ComponentActivatorAbstractBase {
private static final Logger log = LoggerFactory.getLogger(PacketHandler.class);
 
public Object[] getImplementations() {
    log.trace("Getting Implementations");
 
    Object[] res = { PacketHandler.class };
    return res;
    }
 
public void configureInstance(Component c, Object imp, String containerName) {
    log.trace("Configuring instance");
 
    if (imp.equals(PacketHandler.class)) {
                // Define exported and used services for PacketHandler component.
 
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            props.put("salListenerName", "mypackethandler");
 
                // Export IListenDataPacket interface to receive packet-in events.
            c.setInterface(new String[] {IListenDataPacket.class.getName()}, props);
 
                // Need the DataPacketService for encoding, decoding, sending data packets
            c.add(createContainerServiceDependency(containerName).setService(
            IDataPacketService.class).setCallbacks(
                    "setDataPacketService", "unsetDataPacketService")
                    .setRequired(true));
 
                // Need FlowProgrammerService for programming flows
            c.add(createContainerServiceDependency(containerName).setService(
            IFlowProgrammerService.class).setCallbacks(
                    "setFlowProgrammerService", "unsetFlowProgrammerService")
                    .setRequired(true));
 
                // Need SwitchManager service for enumerating ports of switch
            c.add(createContainerServiceDependency(containerName).setService(
            ISwitchManager.class).setCallbacks(
                    "setSwitchManagerService", "unsetSwitchManagerService")
                    .setRequired(true));
        }
 
    }
}