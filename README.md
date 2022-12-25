## HTML TO PDF (Create Pdf from Html or Text)

#### How to run service
#
Build Service:

    ./gradlew build

Run Service: http://localhost:8080/

    ./gradlew bootRun

Curl For Test Service:
    
    curl --location --request POST 'http://localhost:8080/pdf/create' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "html": "",
        "width": 300,
        "height": 300
    }'

Option Description

    html : html or text

    width: width only number

    height: height only number