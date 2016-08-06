package com.landoop.avrogenerator.messages;

public class AvroTransferWise {

  // While raising money requests - the user sees automatically a message (so maybe we'll build kafka-streams & rocksDB)
  // Example msg: `You will receive approximately €115.53`
  static final String REQUEST_MONEY_SCHEMA = "{`type`:`record`,`name`:`com.landoop.demo.tranferwise`," +
          "`doc`:`Customer raising money requests`," +
          "`fields`:[" +
          "{`name`:`customerID`,`type`:`long`,`doc`:`The unique ID of the customer requesting money`}," +
          "{`name`:`amount`,`type`:`long`,`doc`:`How much a customer needs`}," +
          "{`name`:`currency`,`type`:`string`,`doc`:`What currency the customer needs`}," +
          "{`name`:`reason`,`type`:`string`,`doc`:`What's the money for`}" +
          "{`name`:`targetName`,`type`:`string`,`doc`:`The recipient's account name`}" +
          "{`name`:`targetCurrency`,`type`:`string`,`doc`:`The recipient's account currency`}" +
          "{`name`:`targetIBAN`,`type`:`string`,`doc`:`The recipient's 22 character IBAN`}" +
          "{`name`:`requestUrl`,`type`:`string`,`doc`:`A unique payment url - such as https://transferwise.com/pay#/191465ce-7e22-4fff-9775-68288031173b`}" +
          "{`name`:`timestamp`,`type`:`long`,`doc`:`UTC Epoch (msec since 1970)`}" +
          "]}";

  // Money-Transfer events guarantee a rate for 24 hours - so timestamp is important
  static final String TRANSFER_MONEY_SCHEMA = "{`type`:`record`,`name`:`com.landoop.demo.tranferwise`," +
          "`doc`:`Customer transferring money to an account`," +
          "`fields`:[" +
          "{`name`:`customerID`,`type`:`long`,`doc`:`The unique ID of the customer transferring money`}," +
          "{`name`:`sourceAmount`,`type`:`long`,`doc`:`How much a customer needs`}," +
          "{`name`:`sourceCurrency`,`type`:`string`,`doc`:`What currency the customer needs`}," +
          "{`name`:`reason`,`type`:`string`,`doc`:`What's the money for`}" +
          "{`name`:`targetName`,`type`:`string`,`doc`:`The recipient's account name`}" +
          "{`name`:`targetCurrency`,`type`:`string`,`doc`:`The recipient's account currency`}" +
          "{`name`:`targetIBAN`,`type`:`string`,`doc`:`The recipient's 22 character IBAN`}" +
          "{`name`:`targetBankCode`,`type`:`string`,`doc`:`Bank code (BIC/SWIFT)`}" +
          "{`name`:`targetAmount`,`type`:`long`,`doc`:`The target amount - that is guaranteed if the funds are send within 24 hours`}" +
          "{`name`:`reference`,`type`:`string`,`doc`:`The reference number to be used if bank transfer is used to deposit funds. i.e. P1783075`}" +
          "{`name`:`timestamp`,`type`:`long`,`doc`:`UTC Epoch (msec since 1970)`}" +
          "]}";

  static final String[] currencies = {
          "EUR",
          "GBP",
          "USD",
          "AED",
          "AUD",
          "BGN",
          "BRL",
          "CAD",
          "CHF",
          "CNY",
          "CZK",
          "DKK",
          "GEL",
          "HKD",
          "HUF",
          "KRW",
          "MAD",
          "MXN",
          "MYR",
          "NOK",
          "NZD",
          "PHP",
          "PKR",
          "PLN",
          "RON",
          "SEK",
          "SGD"
  };

  static final String[] paymentTypes = {"Debit card", "Credit card", "Bank transfer"};

}
