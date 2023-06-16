# Sensor Data Collector App MSD Project

## Description

This is the repository for the data collection application portion of the Medical Software Development Module.
The task was to utilise a digital biomarker sensor through an android application in order 
to record patient data, analyse it and transmit it to a backend service.

The field of medicine and biomedical research is undergoing a transformation due 
to digital biomarkers. Their use is becoming more widespread, with increasing 
popularity, and the widespread use of wearable technology. Digital biomarkers encompass 
measurable physiological and behavioral indicators that are collected through 
portable, wearable, implantable, or digestible digital devices.

The use case and scientific question that digital biomarkers can answer are typically
related to providing a prognosis, or to support a diagnosis of a condition. Monitoring
a patient recovering from surgery could also be one specific use case. Our project aims
to help clinicians monitor the physical well-being of patients through analysing 
their ability to walk, and to record instances where they may take falls.
The application leverages the accelerometer sensor on the android phone in 
order to detect if an older patient, or a patient with a walking impairment has 
taken a fall, or if their condition and ability to walk is deteriorating over time. 
One use case could be the monitoring of a patients gait in order to track their motor
function recovery after a stroke for example(1). Monitoring rehabilitation after an operation
concerning spinal-cord  injuries or lower limb amputations is also a possible
use case for gait analysis (2). Patients with hip dysplasia, geriatric  disorders, or osteoarthritis
could also be monitored to analyse effects of an intervention. 

Geriatric patients are especially prone to injury from falls due to frailty, and lower
physiological reserve, which affects their ability to walk up stairs or tackle uneven ground(3).
Our application focuses on the detection and monitoring of falls that a patient who has a walking impairment, or
recovering from a surgery such as a hip replacement (hip prosthesis) may take, and to monitor their 
condition remotely. In this way the clinician will be more informed about the rehabilitation process, and
can make data driven decisions concerning interventions. A key component of the application is the ability to detect
falls in real-time. Future versions of the app could implement an alert system. There are two main parts to the applications funtionality, 
the sensor data collection service and the sensor data collection application.  
 
## Functionalities of Sensor Data Collection App:

1. Collect sensor data
2. Use accelerometer data to detect falls in real time
6. Send data to data collection service

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- Java 8 or higher
- Maven
- Android Studio

### Installing

1. Clone the repository
    ```bash
    git clone https://github.com/molinamarcvdb/AndroidAppMSD.git
    ```
2. Navigate to the project directory
    ```bash
    cd AndroidAppMSD
    ```
3. Build the project
    ```bash
    gradlew.bat assembleDebug
    ```
4. Install the application on device or emulator
    ```bash
    gradlew.bat installDebug
    ```
5. Run the application on device or emulator
    ```bash
    gradlew.bat runDebug
    ```   
    
***

## References

1. Moulaee Conradsson, D., & Bezuidenhout, L. J. (2022). Establishing Accelerometer Cut-Points to Classify Walking Speed in People Post Stroke. Sensors (Basel, Switzerland), 22(11), 4080. https://doi.org/10.3390/s22114080
2. Jarchi, D., Pope, J., Lee, T. K. M., Tamjidi, L., Mirzaei, A., & Sanei, S. (2018). A Review on Accelerometry-Based Gait Analysis and Emerging Clinical Applications. IEEE reviews in biomedical engineering, 11, 177–194. https://doi.org/10.1109/RBME.2018.2807182
3. Vaishya, R., & Vaish, A. (2020). Falls in Older Adults are Serious. Indian journal of orthopaedics, 54(1), 69–74. https://doi.org/10.1007/s43465-019-00037-x

## Authors and acknowledgment
Marc Molina Van den Bosch, Caterina Montalbano, Lukasz Kaczmarek, Marco De Luca, Paul Stehberger, Paul Tanner

## License
This project is licensed under the MIT License - see the [LICENSE.md](https://github.com/yourusername/experiment-data-management-service/blob/main/LICENSE.md) file for details
