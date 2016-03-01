/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ambari.server.controller.utilities.state;


import org.apache.ambari.server.state.Host;
import org.apache.ambari.server.state.HostState;
import org.apache.ambari.server.state.ServiceComponent;
import org.apache.ambari.server.state.ServiceComponentHost;
import org.apache.ambari.server.state.State;
import org.junit.Assert;

import java.util.HashMap;
import java.util.Map;

public class HiveServiceCalculatedStateTest extends GeneralServiceCalculatedStateTest{
  @Override
  protected String getServiceName() {
    return "HIVE";
  }

  @Override
  protected ServiceCalculatedState getServiceCalculatedStateObject() {
    return new HiveServiceCalculatedState();
  }

  @Override
  protected void createComponentsAndHosts() throws Exception {
    ServiceComponent masterComponent = service.addServiceComponent("HIVE_METASTORE");
    ServiceComponent secondMasterComponent = service.addServiceComponent("HIVE_SERVER");
    ServiceComponent thirdMasterComponent = service.addServiceComponent("WEBHCAT_SERVER");
    ServiceComponent fourMasterComponent = service.addServiceComponent("MYSQL_SERVER");
    ServiceComponent clientComponent = service.addServiceComponent("HIVE_CLIENT");

    for (String hostName: hosts){
      clusters.addHost(hostName);
      Host host = clusters.getHost(hostName);

      Map<String, String> hostAttributes = new HashMap<>();
      hostAttributes.put("os_family", "redhat");
      hostAttributes.put("os_release_version", "6.3");
      host.setHostAttributes(hostAttributes);
      host.setState(HostState.HEALTHY);
      host.persist();
      clusters.mapHostToCluster(hostName, clusterName);

      ServiceComponentHost sch = clientComponent.addServiceComponentHost(hostName);
      sch.setVersion("2.1.1.0");
      sch.setState(State.INSTALLED);

      sch = masterComponent.addServiceComponentHost(hostName);
      sch.setVersion("2.1.1.0");
      sch.setState(State.STARTED);

      sch = secondMasterComponent.addServiceComponentHost(hostName);
      sch.setVersion("2.1.1.0");
      sch.setState(State.STARTED);

      sch = thirdMasterComponent.addServiceComponentHost(hostName);
      sch.setVersion("2.1.1.0");
      sch.setState(State.STARTED);

      sch = fourMasterComponent.addServiceComponentHost(hostName);
      sch.setVersion("2.1.1.0");
      sch.setState(State.STARTED);

    }
  }

  @Override
  public void testServiceState_STARTED() throws Exception {
    updateServiceState(State.STARTED);

    State state = serviceCalculatedState.getState(clusterName, getServiceName());
    Assert.assertEquals(State.STARTED, state);

    service.getServiceComponent("HIVE_METASTORE").getServiceComponentHost(hosts[0]).setState(State.INSTALLED);
    state = serviceCalculatedState.getState(clusterName, getServiceName());
    Assert.assertEquals(State.STARTED, state);

    service.getServiceComponent("HIVE_METASTORE").getServiceComponentHost(hosts[1]).setState(State.INSTALLED);
    state = serviceCalculatedState.getState(clusterName, getServiceName());
    Assert.assertEquals(State.INSTALLED, state);
  }

  @Override
  public void testServiceState_STOPPED() throws Exception {
    updateServiceState(State.INSTALLED);

    State state = serviceCalculatedState.getState(clusterName, getServiceName());
    Assert.assertEquals(State.INSTALLED, state);
  }
}