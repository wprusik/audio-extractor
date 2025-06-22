# Audio Extractor

A simple REST API to extract audio from video.


### Preparation
Set the path to the ffmpeg executable in environment variable **FFMPEG_EXECUTABLE_COMMAND**.

### Run from console
```bash
mvn mn:run
```

### Run on docker
#### Build image
```bash
docker build -t wprusik/audio-extractor .
```

#### Run container
```bash
docker run -d --name audio-extractor -p 8080:8080 wprusik/audio-extractor
```

Then go to Swagger at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).
