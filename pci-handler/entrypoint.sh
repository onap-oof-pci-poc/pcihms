#!/bin/bash

sed -i s/DMAAP_SERVER/$DMAAPSERVER/ /etc/config.json
sed -i s/SDNR_SERVICE/$SDNRSERVICE/ /etc/config.json
sed -i s/OOF_SERVICE/$OOFSERVICE/ /etc/config.json
sed -i s/POLICY_SERVICE/$POLICYSERVICE/ /etc/config.json
sed -i s/PCIMS_SERVICE_HOST/$PCIMS_SERVICE_HOST/ /etc/config.json
sed -i s/MANAGER_API_KEY/$MANAGERAPIKEY/ /etc/config.json
sed -i s/MANAGER_SECRET_KEY/$MANAGERSECRETKEY/ /etc/config.json
sed -i s/PCIMS_API_KEY/$PCIMSAPIKEY/ /etc/config.json
sed -i s/PCIMS_SECRET_KEY/$PCIMSSECRETKEY/ /etc/config.json
sed -i s/SDNR_API_KEY/$SDNRAPIKEY/ /etc/config.json
sed -i s/POLICY_API_KEY/$POLICYAPIKEY/ /etc/config.json
java -jar application.jar

