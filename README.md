# Android Application �Bundle-Express� 
## General
### About
This is a project made by (or partially by) WSN-Team 2012 in the course CSD, which is part of the [Technology Transfer Alliance](http://ttaportal.org/).
### Purpose
One of the goals of the project is to be able to send data upstream from rural areas and to do this DTN communication has been used. Developed BPF(Bundle Protocol Framework)is a tool to achieve it and connect a gateway, mobile unit and central repository. It is independent platform, therefore it must be adapted. In this case, it is implemented in Android platform. This project, �Bundle Express� application, is an example of such service.
### Description
This project is an android application able to carry data called bundle from a gateway to a central repository, there is implemented the BPF platform-dependent classes provided the functionalities for the mobile unit within the context of the WSN-Team 2012 project. 
In particular for the mobile node, the service using discovery mechanism receives and sends bundles to the next point automatically following epidemic routing. It can be a mobile unit with installed application within the same network, or a central repository. Mobile unit receives service notifications and stores bundles into the phone/SD card memory. Bundles are not readable. Data in a readable format can be fetched only via a central repository.
## Build & Install
### Prerequisites
You will need to have ant to compile this in an easy way. To get ant look into how to install it on your platform.

The project is also using a storage option in order to store encrypted bundles. 

### Building ?????
The BPF is included as a submodule and it is built automatically when building the service. It is possible to configure several settings in the configuration file.
Follow the below steps to build service and BPF.

1.  `git clone --recursive https://github.com/WSN-2012/Bundle-Express.git`
2.  `cd Bundle-Express`
3.  `vi config/dtn.config.xml`
4.  `ant`

Note: When building the configuration located in config/dtn.config.xml is copied into the build folder together with the built jar. If you want to change any configuration options please edit the configuration stored in the built folder or rebuild.

### Start/Stop script ???????
To just run the application after building it:

1.  `./run.sh`

To install a start/stop script, follow the below steps:

1.  `cp linux-scripts/bpf-service /etc/init.d/bpf-service`
2.  `/etc/init.d/bpf-service start`
3.  `/etc/init.d/bpf-service stop`

### Monitoring ?????
To be able to monitor the service and automatically restarting it, we suggest using monit. To do this you will need to have set up the start/stop scripts as shown above. Follow the below steps to set the monitoring up:

1.  Install monit on your system. E.g: `sudo apt-get install monit`
2.  `cp linux-scripts/bpf-service.monit /etc/monit/conf.d/bpf-service`
3.  `/etc/init.d/monit restart`

### Start at boot ??????
Do not forget to add the service to start at boot. E.g: `sudo update�rc.d bpf-service defaults`

## License
Copyright 2012 KTH

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
