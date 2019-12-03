package com.ullink.slack.simpleslackapi.impl;

import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mockito.Mockito;

import com.ullink.slack.simpleslackapi.WebSocketContainerProvider;

public class TestSlackWebSocketSessionImpl {

  @Test(expected = IllegalArgumentException.class)
  public void testSendMessageWithNullChanel() throws Exception{
    WebSocketContainerProvider provider = Mockito.mock(WebSocketContainerProvider.class);
    SlackWebSocketSessionImpl webSocketSession = new SlackWebSocketSessionImpl(provider,
        "", false, false, 42L, TimeUnit.MILLISECONDS);
    try {
      webSocketSession.sendMessage(null, "");
    } catch (NullPointerException e) {
      fail("NullPointerException unexpected here");
    }
  }
}
