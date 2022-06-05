# scala-akka-actors-iot

<table>
  <tr>
    <td><img src="https://www.scala-lang.org/resources/img/frontpage/scala-spiral.png" height="100"/></td>
    <td><img src="https://akka.io/resources/images/akka_full_color.svg" width="200"/></td>
  </tr>
</table>

Building Small IoT concept with Scala Akka Actors based on [Getting Started](https://doc.akka.io/docs/akka/current/typed/guide/index.html) guide.

The idea is to implement the IoT system, consisting of temperature sensors (devices) that constantly produce temperature data and gather related statistics.

Devices are gathered in different groups. At the top of all Groups, there is DeviceManager actor.

If the Device is terminated, corresponding Device Actor should be removed from the list.

If the Device Group is terminated, corresponding DeviceGroup Actor should be removed from the list.

Dividing devices into different groups and having separate Actor for each device allows us to have better scaling, error handling and have isolated states for the processes.

In this system there are 6 Actors:

**IotSupervisor** - Root Actor and the scope for other actors.

**IoTNetWorkManager** - dummy Actor just to populate system with groups and devices.

**DeviceManager** - Actor to handle DeviceGroup management.

**DeviceGroup** - Actor that manages Device Actors.

**Device** - Actor that records sensor temperature and emits it on demand.

**DeviceGroupQuery** - Actor for gathering temperature data from Device Actors and self-destructing when finishes.
