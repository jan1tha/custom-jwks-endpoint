# Custom JWKS Endpoint

A custom JSON Web Key Set (JWKS) endpoint service built with Java and Maven, designed for Open Banking compliance and financial services integration.

## Overview

This project provides a RESTful JWKS endpoint service that serves cryptographic keys in JWKS format, which is essential for JWT (JSON Web Token) validation in Open Banking and financial services applications. The service aggregates keys from multiple sources including external JWKS endpoints and local bank certificates.

## Features

- **JWKS Endpoint**: Provides a standardized JWKS endpoint at `/services/jwks/endpoint`
- **Health Check**: Includes a heartbeat endpoint at `/services/jwks/heartbeat`
- **Certificate Management**: Supports both external JWKS sources and local bank certificates
- **Open Banking Compliance**: Designed specifically for Open Banking standards
- **RESTful API**: Built with JAX-RS for modern web service standards

## Architecture

The service is built as a Java web application using:
- **Java 11** with Maven build system
- **JAX-RS** for REST API implementation
- **Apache CXF** as the web service framework
- **JSON** for data exchange
- **SLF4J** for logging

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Apache Tomcat or similar servlet container
- Access to external JWKS endpoints (if configured)

## Configuration

### Configuration Files

The service expects configuration files in the following locations relative to `carbon.home`:

1. **`repository/conf/finance/config.properties`** - Contains external endpoint configurations
   ```properties
   DCR_JWKS_REG_ENDPOINT=https://your-external-jwks-endpoint.com/jwks
   ```

2. **`repository/conf/finance/cert-list.json`** - Contains local bank certificates in JWKS format
   ```json
   [
     {
       "kty": "RSA",
       "kid": "your-key-id",
       "use": "sig",
       "alg": "RS256",
       "n": "your-modulus",
       "e": "your-exponent"
     }
   ]
   ```

## Building the Project

### Clone the Repository
```bash
git clone <repository-url>
cd custom-jwks-endpoint
```

### Build with Maven
```bash
mvn clean install
```

The build will create a WAR file named `openbankingutility#v1.war` in the `target/` directory.

### Build Configuration

The project uses Maven with the following key configurations:
- **Packaging**: WAR (Web Application Archive)
- **Java Version**: 11
- **Dependencies**: JAX-RS, JAXB, JSON, SLF4J, and custom ARB common library

## Deployment

### Deploy to WSO2 Identity Server

This application is designed to be deployed as a web application within WSO2 Identity Server. Follow these steps for deployment:

#### Prerequisites
- WSO2 Identity Server 5.11.0 or higher
- Java 11 or higher
- Maven 3.6 or higher

#### Deployment Steps

1. **Build the Application**
   ```bash
   mvn clean install
   ```

2. **Deploy to WSO2 Identity Server**
   
   **Option 1: Hot Deployment (Development)**
   - Copy the generated WAR file to the WSO2 IS webapps directory:
     ```bash
     cp target/openbankingutility#v1.war $WSO2_IS_HOME/repository/deployment/server/webapps/
     ```
   - The application will be automatically deployed and available at:
     ```
     https://localhost:9443/openbankingutility%23v1/services/jwks/
     ```

   **Option 2: Manual Deployment (Production)**
   - Stop WSO2 Identity Server:
     ```bash
     $WSO2_IS_HOME/bin/wso2server.sh stop
     ```
   - Copy the WAR file:
     ```bash
     cp target/openbankingutility#v1.war $WSO2_IS_HOME/repository/deployment/server/webapps/
     ```
   - Start WSO2 Identity Server:
     ```bash
     $WSO2_IS_HOME/bin/wso2server.sh start
     ```

3. **Verify Deployment**
   - Check the WSO2 IS console logs for successful deployment
   - Access the health check endpoint:
     ```
     https://localhost:9443/openbanking/utility/services/jwks/heartbeat
     ```

#### Configuration in WSO2 Identity Server

1. **Set Carbon Home**
   The application uses the WSO2 IS carbon home for configuration. Ensure the following directory structure exists:
   ```
   $WSO2_IS_HOME/repository/conf/finance/
   ├── config.properties
   └── cert-list.json
   ```

2. **Update WSO2 IS Configuration**
   - Add the following to `$WSO2_IS_HOME/repository/conf/carbon.xml` if needed:
     ```xml
     <CarbonHome>${carbon.home}</CarbonHome>
     ```

3. **Security Configuration**
   - The application inherits WSO2 IS security settings
   - Ensure proper SSL/TLS configuration in `$WSO2_IS_HOME/repository/conf/tomcat/catalina-server.xml`
   - Configure authentication if required in `$WSO2_IS_HOME/repository/conf/identity/identity.xml`

#### Access URLs

After deployment, the service will be available at:
- **JWKS Endpoint**: `https://localhost:9443/openbanking/utility/services/jwks/endpoint`
- **Health Check**: `https://localhost:9443/openbanking/utility/services/jwks/heartbeat`

**Note**: Replace `localhost:9443` with your WSO2 IS host and port configuration.

#### Troubleshooting WSO2 IS Deployment

1. **Deployment Failed**
   - Check WSO2 IS logs: `$WSO2_IS_HOME/repository/logs/wso2carbon.log`
   - Verify WAR file permissions and location
   - Ensure Java version compatibility

2. **Service Not Accessible**
   - Verify WSO2 IS is running: `$WSO2_IS_HOME/bin/wso2server.sh status`
   - Check webapp deployment status in WSO2 IS management console
   - Verify URL patterns and servlet mappings

3. **Configuration Issues**
   - Ensure `carbon.home` property is correctly set
   - Verify configuration files exist in `$WSO2_IS_HOME/repository/conf/finance/`
   - Check WSO2 IS carbon configuration

## API Endpoints

### 1. JWKS Endpoint
- **URL**: `GET /services/jwks/endpoint`
- **Purpose**: Returns the complete JWKS containing all available keys
- **Response**: JSON object with a `keys` array containing JWK objects
- **Content-Type**: `application/json`

**Example Response:**
```json
{
  "keys": [
    {
      "kty": "RSA",
      "kid": "key-1",
      "use": "sig",
      "alg": "RS256",
      "n": "modulus-value",
      "e": "AQAB"
    }
  ]
}
```

### 2. Health Check
- **URL**: `GET /services/jwks/heartbeat`
- **Purpose**: Service health monitoring
- **Response**: Simple text message indicating service status
- **Content-Type**: `application/json`

**Example Response:**
```json
"Al Rayan Bank JWKS service up!"
```

## Error Handling

The service provides comprehensive error handling with structured JSON error responses. All errors include:
- `error`: Error code identifier
- `error_description`: Human-readable error description
- `http_status`: HTTP status code
- `timestamp`: Unix timestamp of when the error occurred

### Common Error Responses

#### Configuration Error (500)
```json
{
  "error": "CONFIGURATION_ERROR",
  "error_description": "System property 'carbon.home' is not configured",
  "http_status": 500,
  "timestamp": 1703123456789
}
```

#### File Not Found (404)
```json
{
  "error": "FILE_NOT_FOUND",
  "error_description": "Bank certificates configuration file does not exist",
  "http_status": 404,
  "timestamp": 1703123456789
}
```

#### External Endpoint Error (502)
```json
{
  "error": "EXTERNAL_ENDPOINT_ERROR",
  "error_description": "Failed to retrieve JWKS from external endpoint",
  "http_status": 502,
  "timestamp": 1703123456789
}
```

#### No Keys Available (503)
```json
{
  "error": "NO_KEYS_AVAILABLE",
  "error_description": "Both external JWKS endpoint and local certificates are unavailable",
  "http_status": 503,
  "timestamp": 1703123456789
}
```

### Error Recovery

The service implements graceful degradation:
- If external JWKS endpoint fails, it continues with local certificates
- If local certificates fail, it continues with external keys
- Only returns an error if both sources are unavailable

## Usage Examples

### cURL Commands

**Get JWKS:**
```bash
curl -X GET "https://localhost:9443/openbanking/utility/services/jwks/endpoint" \
     -H "Accept: application/json" \
     -k
```

**Health Check:**
```bash
curl -X GET "https://localhost:9443/openbanking/utility/services/jwks/heartbeat" \
     -H "Accept: application/json" \
     -k
```

**Note**: The `-k` flag is used to skip SSL certificate verification for self-signed certificates in development. Remove this flag in production or use proper certificates.

## Development

### Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── com/wso2/ob/webapp/utility/
│   │       ├── services/
│   │       │   ├── JWKSService.java      # Main REST service
│   │       │   └── MockInternalService.java
│   │       └── utils/
│   │           └── JWKSUtil.java         # Utility functions
│   └── webapp/
│       └── WEB-INF/
│           ├── web.xml                   # Web application configuration
│           └── cxf-servlet.xml          # CXF servlet configuration
```

### Adding New Features

1. **New Endpoints**: Add methods to `JWKSService.java` with appropriate JAX-RS annotations
2. **New Utilities**: Extend `JWKSUtil.java` with additional helper methods
3. **Configuration**: Add new properties to `config.properties` and update `JWKSUtil.java`

## Troubleshooting

### Common Issues

1. **Access Denied Not Set**
   - Error: "Carbon home property is not set"
   - Solution: Set `-Dcarbon.home=/path/to/carbon/home` JVM argument

2. **Configuration Files Missing**
   - Error: "Error trying to get bank certs from file"
   - Solution: Ensure `cert-list.json` exists in the configured path

3. **External Endpoint Unreachable**
   - Error: HTTP error responses from external JWKS endpoint
   - Solution: Verify network connectivity and endpoint URL in `config.properties`

### Logging

The service uses SLF4J for logging. Enable debug logging to see detailed request/response information:
You can configure logging from WSO2 <IAM-HOME>/repository/conf/log4j2.properties
```bash
logger.jwks-util.name=com.wso2.ob.webapp.utility.services
logger.jwks-util.level=DEBUG
```

then append add the `jwks-util` to loggers

```bash
loggers = <ALL LOGGERS>, jwks-util
```

## Security Considerations

- **HTTPS**: Always use HTTPS in production environments
- **Access Control**: Implement appropriate authentication and authorization
- **Key Rotation**: Regularly rotate cryptographic keys
- **Audit Logging**: Monitor and log all JWKS requests
- **Input Validation**: Validate all external inputs and configurations

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Refer to Open Banking documentation for compliance requirements

## Version History

- **1.0-SNAPSHOT**: Initial release with basic JWKS functionality
  - JWKS endpoint implementation
  - Health check endpoint
  - External and local certificate support
  - Open Banking compliance features
