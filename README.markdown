Fat Zebra Java Library
======================

Release version 1.3

This library provides the basic functionality for developers to integrate their applications against the Fat Zebra API.

Currently the library provides the following functionality:

 * Create a Purchase
 * Find a Purchase
 * Refund a Purchase
 * Pre-auth a purchase (do not capture the purchase)
 * Capture a pre-authorised purchase

Usage
-----

Within your application you can use the following code to perform transactions:

```java

// Setup - you only need to do this once upon application initialization
FatZebra.username = "TEST";
FatZebra.token = "TEST";
FatZebra.sandbox = true; // Omit this for production

// Alternatively you can setup a Gateway Context:

GatewayContext ctx = new GatewayContext();

int cardExpiryMonth = 7;
int cardExpiryYear = 2023;

HashMap<String,Object> card_data = new HashMap<String, Object>();
card_data.put("card_number", "5123456789012346");
card_data.put("card_holder", "James Smith");
card_data.put("cvv", "123");
card_data.put("card_expiry", String.format("%d/%d", cardExpiryMonth, cardExpiryYear));

try {
    Purchase p = Purchase.create(
                              100, // The amount, as an integer. i.e. $150.75 would be 15075
                              card_data, // A HashMap of the card data we defined above
                              "my_reference", // Your order reference, such as an invoice number
                              "1.2.3.4", // The customers IP address
                              "AUD" // The currency code (such as AUD, USD, EUR, GBP etc)
                            );
} catch(IOException ex) {
    System.err.println("IOException in purchase request.");
    ex.printStackTrace();
} catch(APIError ex) {
    System.err.println("API Error in purchase request.");
    ex.printStackTrace();
} catch(NetworkError ex) {
    System.err.println("Network Error in purchase request.");
    ex.printStackTrace();
}

// If no exceptions are raised then you can inspect the purchase object:

if (p.successful) {
  // Ok we are good to go with the successful transactions.
  System.out.println(String.format("Transaction successful: %s", p.toString());
} else {
  // The transaction was declined in the bank network
  System.err.println(String.format("Transaction failed. Message: %s. Transaction: %s", p.message, p.toString()));
}

// If the transaction is successful you can refund it:

try {
    boolean refunded = p.refund(
                            100, // Amount of refund - must be less then or equal to the transaction amount or remaining amount should any refunds be already made
                            "my_refund_reference" // Optional - if omitted we will use the purchase original reference.
                            );
} catch(IOException ex) {
  System.err.println("IOException in refund request.");
  ex.printStackTrace();
} catch(APIError ex) {
  System.err.println("API Error in refund request.");
  ex.printStackTrace();
} catch(NetworkError ex) {
  System.err.println("Network Error in refund request.");
  ex.printStackTrace();
}

System.out.println(String.format("Refund successful: %s", refunded));

// Alternatively you can create a refund without the purchase
try {
    Refund r = Refund.create(
                          100, // Amount as integer
                          "071-P-ABC123D5", // The transaction ID
                          "my refund ref" // Optional
    );
} catch(IOException ex) {
    System.err.println("IOException in refund request.");
    ex.printStackTrace();
} catch(APIError ex) {
    System.err.println("API Error in refund request.");
    ex.printStackTrace();
} catch(NetworkError ex) {
    System.err.println("Network Error in refund request.");
    ex.printStackTrace();
}

System.out.println(r.toString());

// Finally you can lookup transactions:

try {
    Purchase p2 = Purchase.find("my_reference"); // You can use the merchant reference, or the Fat Zebra Transaction ID such as 071-P-XXMRSYH6
    p2.refund(100);
} catch(IOException ex) {
    System.err.println("IOException in purchase lookup or refund request.");
    ex.printStackTrace();
} catch(APIError ex) {
    System.err.println("API Error in purchase lookup or refund request.");
    ex.printStackTrace();
} catch(NetworkError ex) {
    System.err.println("Network Error in purchase lookup or refund request.");
    ex.printStackTrace();
}


```


Authorisations and Capture
--------------------------

It is possible to pass a flag to the `Purchase.create()` method to perform an auth/capture transaction. Once a transaction is authorised it can be captured up to 72 hours
after the successful auth.

```java

// Setup - you only need to do this once upon application initialization
FatZebra.username = "TEST";
FatZebra.token = "TEST";
FatZebra.sandbox = true; // Omit this for production


int cardExpiryMonth = 7;
int cardExpiryYear = 2023;

HashMap<String,Object> card_data = new HashMap<String, Object>();
card_data.put("card_number", "5123456789012346");
card_data.put("card_holder", "James Smith");
card_data.put("cvv", "123");
card_data.put("card_expiry", String.format("%d/%d", cardExpiryMonth, cardExpiryYear));

try {
    Purchase p = Purchase.create(
                              100, // The amount, as an integer. i.e. $150.75 would be 15075
                              card_data, // A HashMap of the card data we defined above
                              "my_reference", // Your order reference, such as an invoice number
                              "1.2.3.4", // The customers IP address
                              "AUD", // The currency code (such as AUD, USD, EUR, GBP etc)
                              false // Indicates whether the transaction should be captured or not - default = true
                            );
}

// Capture the transaction:
if (p.successful) {
    boolean captured = p.capture(10); // The capture amount can be less then or equal to the authorisation amount
    if (captured) {
        System.out.println(String.format("Transaction captured for %d", p.captured_total));
    }
}

```


Exceptions
----------

There are two exception classes defined:

  * NetworkError - raised when connectivity issues are presented, such as a timeout, resolve or connect error
  * API Error - raised when there is an error response from the API, such as validation issues, uniqueness colissions etc


3rd Party Dependencies
----------------------

This library depends on the following external dependencies:

 * Google Gson version 2.2.4
 * Apache HTTP Commons commons-codec version 1.6

These dependencies have been included in the project and packaged within the included jar.
