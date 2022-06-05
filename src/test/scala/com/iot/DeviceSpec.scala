package com.iot.device

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike

class DeviceSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {
  import Device._

  "Device actor" must {

    "reply with empty reading if no temperature is known" in {
      val probe = createTestProbe[RespondTemperature]()
      val deviceActor = spawn(Device("test-group", "device1"))

      deviceActor ! Device.ReadTemperature(requestId = 42, probe.ref)
      val response = probe.receiveMessage()
      response.requestId should ===(42)
      response.value should ===(None)
    }

    "reply with recorded temperature value" in {
      val recordProbe = createTestProbe[TemperatureRecorded]()
      val readProbe = createTestProbe[RespondTemperature]()
      val deviceActor = spawn(Device("test-group", "device2"))

      deviceActor ! Device.RecordTemperature(requestId = 1, 42, recordProbe.ref)
      recordProbe.expectMessage(Device.TemperatureRecorded(requestId = 1))

      deviceActor ! Device.ReadTemperature(requestId = 2, readProbe.ref)
      readProbe.expectMessage(Device.RespondTemperature(requestId = 2,  deviceId = "device2", Some(42)))

      deviceActor ! Device.RecordTemperature(requestId = 3, 55, recordProbe.ref)
      recordProbe.expectMessage(Device.TemperatureRecorded(requestId = 3))

      deviceActor ! Device.ReadTemperature(requestId = 4, readProbe.ref)
      readProbe.expectMessage(Device.RespondTemperature(requestId = 4, deviceId = "device2", Some(55)))
    }
  }
}
