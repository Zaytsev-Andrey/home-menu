FROM bellsoft/liberica-openjdk-alpine:25-cds AS builder
WORKDIR /application

# Layer 1: Gradle wrapper only (almost never changes)
COPY gradlew .
COPY gradle/ gradle/

# Layer 2: Download Gradle distribution (cached until wrapper version changes)
RUN chmod +x gradlew && ./gradlew --version --no-daemon

# Layer 3: Build files (change when you add/update dependencies)
COPY build.gradle settings.gradle ./
COPY gradle/libs.versions.toml gradle/

# Layer 4: Resolve dependencies (cached until build files change)
RUN ./gradlew dependencies --no-daemon || true

COPY . .

RUN chmod +x gradlew && ./gradlew :recipe-service:clean :recipe-service:bootJar -x test -x jooqCodegen  --no-daemon


FROM bellsoft/liberica-openjre-alpine:25-cds AS layers
WORKDIR /application

COPY --from=builder /application/recipe-service/build/libs/*.jar app.jar

#RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted
RUN java -Djarmode=tools -jar app.jar extract --destination extracted

FROM bellsoft/liberica-openjre-alpine:25-cds
VOLUME /tmp

RUN adduser -S spring-user
USER spring-user

WORKDIR /application

# Layer 1: Dependencies (cached until you change build.gradle)
COPY --from=layers /application/extracted/lib/ lib/

# Layer 2: Application jar (changes on every build)
COPY --from=layers /application/extracted/app.jar app.jar

ENV JAVA_ERROR_FILE_OPTS="-XX:ErrorFile=/tmp/java_error.log"
ENV JAVA_HEAP_DUMP_OPTS="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp"
ENV JAVA_ON_OUT_OF_MEMORY_OPTS="-XX:+CrashOnOutOfMemoryError"
ENV JAVA_NATIVE_MEMORY_TRACKING_OPTS="-XX:NativeMemoryTracking=summary -XX:+UnlockDiagnosticVMOptions -XX:+PrintNMTStatistics"
ENV JAVA_GC_LOG_OPTS="-Xlog:gc*,safepoint:/tmp/gc.log::filecount=10,filesize=100M"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java \
   $JAVA_HEAP_DUMP_OPTS \
   $JAVA_ON_OUT_OF_MEMORY_OPTS \
   $JAVA_ERROR_FILE_OPTS \
   $JAVA_NATIVE_MEMORY_TRACKING_OPTS \
   $JAVA_GC_LOG_OPTS \
   -jar app.jar"]
