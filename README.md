**CoinCalculator Backend (BE)**

The CoinCalculator Backend (BE) is a RESTful API that supports the CoinCalculator Frontend (FE) by performing necessary calculations, handling data storage, and providing endpoints for the frontend application.

**Installation**

Clone the repository and run the dockerfile. 

**API Endpoints**

1. /coincalculator
Method: GET

Description: Calculates the least number of coin required using coin denominations provided to reach target amount input.

Request Body Example:

json

{

  "targetAmount": 103,

  "coinDenominations": [1, 2, 50]

}

Response Example:

json

{

  [0.01, 0.01, 0.01, 1, 1, 5]
  
}

