# Extended-Multi-Server-Chat-System
The extended Multi-Server Chat System. The project primarily focuses on addressing the following challenges of the system: 
• Security 
• Failure 
• Scalability


1. Security
To address security at least three main issues must be addressed.
1) All communications (between servers and between clients and servers) in the system should be happening
via secure channels (encrypted communication).
• Recommendation: Use TSL/SSL TCP connections with self-signed certificates.
2) You need to provide an authentication mechanism for your project. Only registered and authorized users with valid username and password can log in into the system (No need to impelement a registration method. This can be manual.)
• Recommendation: Create a central authentication server with some predefined users.
3) You need to add the authentication feature to the given chat client program to support your authentication
mechanism.

2.Failure Handling

If a chat server crashes or stops responding, other chat servers should detect this situation. All chatrooms on that server should be removed from the list and clients should not be redirected to that server anymore.
• Recommendation: Use heartbeat signals generated at regular intervals to check if the other servers are working correctly.
You do not need to handle failures for the chat clients. Clients are simply getting disconnected in the case of a chat server crash. The chatrooms and other data associated to the user will be deleted in a similar way to the previous project.

3.Scalability

For the sake of scalability, your system has this feature that the system admin can add new servers to the system manually.
The newly added servers become fully functional components of the chat system and clients can directly connect to these chat servers. Thus, these servers participate in all the Lock and Release communications among chat servers to handle client identities and chat rooms.
• Recommendation: Add new JSON messages to the communication protocol of servers to support this feature that a new server can introduce itself to the system.
