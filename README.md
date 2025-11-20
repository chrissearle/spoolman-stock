# spoolman-stock

Provide an overview of what filament spools require replacing.

Expects to be installed alongside spoolman.

Expects to be located at URL_TO_SPOOLMAN + '/stock'.

Is based on spool first used - rather than weight remaining (I don't use the weight functions as I have no system that
will auto update this information - I only flag "started" and "empty" by hand)

## Spoolman configuration

Expects two extra fields:

* shop: String - the url where this filament can be purchased
* stock: Int - how many to have on hand

It does expect a filament color value to be defined.

## Environment variables

```shell
SPOOLMAN_API_PREFIX=https://HOSTNMAME/api/v1
SPOOLMAN_SPOOL_PREFIX=https://HOSTNMAME/stock/scan/spool/
SPOOLMAN_LOCATION_PREFIX=https://HOSTNMAME/stock/scan/location/
SPOOLMAN_CLEAR_URL=https://HOSTNMAME/stock/scan/clear
```

* `SPOOLMAN_API_PREFIX` - the root of the Spoolman API
* `SPOOLMAN_SPOOL_PREFIX` - the prefix for scanning a spool - will get the spool ID appended
* `SPOOLMAN_LOCATION_PREFIX` - the prefix for scanning a location - will get the location name appended
* `SPOOLMAN_CLEAR_URL` - the URL to clear the scan session

## Usage

* HTML page to show what should be reordered: `https://HOSTNAME/stock/`

* Location scan page: `https://HOSTNAME/stock/scan/location/LOCATION_NAME`
* Spool scan page: `https://HOSTNAME/stock/scan/spool/SPOOL_ID`
* Clear scan page: `https://HOSTNAME/stock/scan/clear`

* Location api page: `https://HOSTNAME/stock/api/locations`
* Spool api page: `https://HOSTNAME/stock/api/spools`

## Scanning

Generate QR codes for each location and spool using the API pages.

Scanning a location or a spool QR code will register that into the session.

If both a location and a spool are registered in the session, the spool will be moved to that location.

Sessions live for 5 minutes - or you can call the clear scan page to empty the session.

So - you can scan a location and then a series of spools to move them all to that location.

Or you can scan a spool first then a location to move that spool.
