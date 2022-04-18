#!/bin/bash
aws cloudformation create-stack --region us-west-2 --stack-name CatalogItemVersions --template-body file://src/resources/yaml/CatalogItemVersions.yaml --capabilities CAPABILITY_IAM
aws cloudformation create-stack --region us-west-2 --stack-name PublishingStatus --template-body file://src/resources/yaml/PublishingStatus.yaml --capabilities CAPABILITY_IAM
