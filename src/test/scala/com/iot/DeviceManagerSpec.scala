package com.iot

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike
import Device._
import DeviceManager._

class DeviceManagerSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  "be able to list active devices" in {
    val registeredProbe = createTestProbe[DeviceRegistered]()
    val groupActor = spawn(DeviceGroup("group"))

    groupActor ! RequestTrackDevice("group", "device1", registeredProbe.ref)
    registeredProbe.receiveMessage()

    groupActor ! RequestTrackDevice("group", "device2", registeredProbe.ref)
    registeredProbe.receiveMessage()

    val deviceListProbe = createTestProbe[ReplyDeviceList]()
    groupActor ! RequestDeviceList(
      requestId = 0,
      groupId = "group",
      deviceListProbe.ref
    )
    deviceListProbe.expectMessage(
      ReplyDeviceList(requestId = 0, Set("device1", "device2"))
    )
  }

  "be able to list active devices after one shuts down" in {
    val registeredProbe = createTestProbe[DeviceRegistered]()
    val groupActor = spawn(DeviceGroup("group"))

    groupActor ! RequestTrackDevice("group", "device1", registeredProbe.ref)
    val registered1 = registeredProbe.receiveMessage()
    val toShutDown = registered1.device

    groupActor ! RequestTrackDevice("group", "device2", registeredProbe.ref)
    registeredProbe.receiveMessage()

    val deviceListProbe = createTestProbe[ReplyDeviceList]()
    groupActor ! RequestDeviceList(
      requestId = 0,
      groupId = "group",
      deviceListProbe.ref
    )
    deviceListProbe.expectMessage(
      ReplyDeviceList(requestId = 0, Set("device1", "device2"))
    )

    toShutDown ! Passivate
    registeredProbe.expectTerminated(
      toShutDown,
      registeredProbe.remainingOrDefault
    )

    // using awaitAssert to retry because it might take longer for the groupActor
    // to see the Terminated, that order is undefined
    registeredProbe.awaitAssert {
      groupActor ! RequestDeviceList(
        requestId = 1,
        groupId = "group",
        deviceListProbe.ref
      )
      deviceListProbe.expectMessage(
        ReplyDeviceList(requestId = 1, Set("device2"))
      )
    }
  }
}
