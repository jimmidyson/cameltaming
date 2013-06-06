package com.github.jimmidyson.cameltaming;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.util.Dictionary;

public class OverridePropertiesTest extends CamelBlueprintTestSupport {

  private static final String ENDPOINT_IN = "activemq:queue:in";
  private static final String ENDPOINT_OUT = "mock:out";

  private static final String persistentId = "com.github.jimmidyson.cameltaming";

  @EndpointInject(uri = ENDPOINT_OUT)
  private MockEndpoint outEndpoint;

  @Produce(uri = ENDPOINT_IN)
  private ProducerTemplate template;

  @Override
  protected String getBlueprintDescriptor() {
    return "/OSGI-INF/blueprint/camel-config.xml,/OSGI-INF/blueprint/camel-activemq-config.xml,/OSGI-INF/blueprint/camel-routes.xml";
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  protected String useOverridePropertiesWithConfigAdmin(Dictionary props) throws Exception {
    props.put("broker.url", "vm://localhost?broker.persistent=false");
    props.put("inEndpoint", ENDPOINT_IN);
    props.put("outEndpoint", ENDPOINT_OUT);
    props.put("errorEndpoint", "mock:error");
    return persistentId;
  }

  @Test
  public void sendMessageTest() throws Exception {
    String input = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("messages/brand.xml"));
    outEndpoint.expectedMessageCount(1);
    template.sendBody(input);
    outEndpoint.assertIsSatisfied();
  }

}
