package com.iot

import akka.actor.typed.Behavior
import akka.actor.typed.PostStop
import akka.actor.typed.Signal
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.Behaviors
import com.iot.device.DeviceManager.{
  Command,
  RequestDeviceList,
  ReplyDeviceList,
  RequestTrackDevice,
  DeviceRegistered
}
import akka.actor.typed.ActorRef
import com.iot.device.DeviceManager

object IoTNetWorkManager {
  def apply(deviceManager: ActorRef[Command]): Behavior[Command] =
    Behaviors.setup[Command](context =>
      new IoTNetWorkManager(context, deviceManager)
    )
}

class IoTNetWorkManager(
    context: ActorContext[Command],
    deviceManager: ActorRef[Command]
) extends AbstractBehavior[Command](context) {
  context.log.info("IoT NetWorkManager started")

  deviceManager ! RequestTrackDevice("group-1", "device-1", context.self)
  deviceManager ! RequestDeviceList(123, "group-1", context.self)

  override def onMessage(msg: Command): Behavior[Command] = {
    msg match {
      case ReplyDeviceList(requestId, list) => {
        context.log.info(
          "IoT NetWorkManager: {} received device list {}",
          requestId,
          list
        )
        this
      }
      case DeviceRegistered(device) => {
        context.log.info(
          "IoT NetWorkManager: device registered {}",
          device.path
        )
        this
      }
    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[Command]] = {
    case PostStop =>
      context.log.info("IoT NetWorkManager stopped")
      this
  }
}
