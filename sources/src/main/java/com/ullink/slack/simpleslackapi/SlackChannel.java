package com.ullink.slack.simpleslackapi;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ullink.slack.simpleslackapi.SlackSession.GetMembersForChannelCallable;


//TODO: a domain object
public class SlackChannel {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlackChannel.class);

    private final boolean direct;
    private String         id;
    private String         name;
    private Set<SlackUser> members = new HashSet<>();
    private GetMembersForChannelCallable getMembersForChannelCallable;
    private String         topic;
    private String         purpose;
    private boolean        isMember;
    private boolean        isArchived;

    public SlackChannel(String id,
                        String name,
                        GetMembersForChannelCallable getMembersForChannelCallable,
                        String topic,
                        String purpose,
                        boolean direct,
                        boolean isMember,
                        boolean isArchived)
    {
        this.id = id;
        this.name = name;
        this.getMembersForChannelCallable = getMembersForChannelCallable;
        this.topic = topic;
        this.purpose = purpose;
        this.direct = direct;
        this.isMember = isMember;
        this.isArchived = isArchived;
    }

    public void addUser(SlackUser user)
    {
        members.add(user);
    }

    void removeUser(SlackUser user)
    {
        members.remove(user);
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public Collection<SlackUser> getMembers() {
        LOGGER.info("members -- size: '{}' -- isEmpty: '{}' -- contents: {}", members.size(), members.isEmpty(), members);
        if (members.isEmpty()) {
            try {
                return getMembersForChannelCallable.setChannelId(id).call();
            } catch (Exception e) {
                return Collections.emptySet();
            }
        }
        return members;
    }

    public String getTopic()
    {
        return topic;
    }

    @Override
    public String toString() {
        return "SlackChannel{" +
                "topic='" + topic + '\'' +
                ", purpose='" + purpose + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getPurpose()
    {
        return purpose;
    }

    public boolean isDirect() {
        return direct;
    }

    public boolean isMember() {
        return isMember;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public SlackChannelType getType()
    {
        //that's a bit hacky
        if (isDirect()) {
            return SlackChannelType.INSTANT_MESSAGING;
        }
        if (id.startsWith("G")) {
            return SlackChannelType.PRIVATE_GROUP;
        }
        return SlackChannelType.PUBLIC_CHANNEL;
    }

    public enum SlackChannelType {
        PUBLIC_CHANNEL, PRIVATE_GROUP, INSTANT_MESSAGING
    }
}
