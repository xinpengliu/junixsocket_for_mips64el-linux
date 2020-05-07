#include <sys/types.h>          
#include <sys/socket.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <string.h>
#include <stdbool.h>
#include <errno.h>
#include <unistd.h>
#define BACK_LOG 1000
#define BUFFER_SIZE 1024
char buffer[BUFFER_SIZE] = {0};
char path[] = "/tmp/aaaaa";
void handleError(char *msg) {
	    perror(msg);
		exit(-1);
}

void bindToAddress(int serverSocket) {
	struct sockaddr_un address;
	address.sun_family = AF_UNIX;
	strncpy(address.sun_path, path, sizeof(path));
	if (remove(path) == -1 && errno != ENOENT) {
		handleError("delete failed");
	}
	if (bind(serverSocket, (struct sockaddr *)&address, sizeof(address)) == -1) {
		handleError("bind failed");
	}
}

void echo(int socket) {
	int numberOfReaded, numberOfWrited = 0;
	while (true) {
		numberOfReaded = recv(socket, buffer, BUFFER_SIZE, 0);
		if (numberOfReaded == -1) {
			handleError("read");
		} else if (numberOfReaded == 0) {
			printf("close\n");
			close(socket);
			return;
		}
		printf("recv:%d，echo", numberOfReaded);
		if (numberOfReaded > 0) {
			numberOfWrited = write(socket, buffer, numberOfReaded);
			printf("  result:%d\n", numberOfWrited);
		}
	}
}

void handleRequest(int serverSocket) {
	int socket = accept(serverSocket, NULL, NULL);
	if (socket == -1) {
		handleError("accept");
	}
	puts("client connect");
	echo(socket);
}
int main(void) {
	int serverSocket = socket(AF_UNIX, SOCK_STREAM, 0);
	if (serverSocket == -1) {
		handleError("socket");
	}
	bindToAddress(serverSocket);
	if (listen(serverSocket, BACK_LOG) == -1) {
		handleError("listen");
	}
	while (true) {
		handleRequest(serverSocket);
	}
}
