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