#!/bin/bash
iname=$(aws autoscaling describe-auto-scaling-groups --region ap-southeast-2 | jq '.AutoScalingGroups[0].Instances[0].InstanceId')
instance_id=`echo $iname | cut -d '"' -f2`
# echo $instance_id
instance_ip=$(aws ec2 describe-instances --instance-ids $instance_id --region ap-southeast-2 | jq '.Reservations[0].Instances[0].PublicIpAddress' | cut -d '"' -f2)
echo $instance_ip

