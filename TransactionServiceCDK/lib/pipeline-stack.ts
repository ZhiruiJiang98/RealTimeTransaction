import { aws_codebuild as codebuild } from "aws-cdk-lib";
import { aws_codepipeline as codepipeline } from "aws-cdk-lib";
import { aws_codepipeline_actions as aws_codepipeline_actions } from "aws-cdk-lib";
import { aws_iam as iam} from 'aws-cdk-lib'
import {aws_kms as kms} from 'aws-cdk-lib'
import { aws_secretsmanager as sm } from "aws-cdk-lib";
import { App, Stack, StackProps, RemovalPolicy, CfnOutput } from 'aws-cdk-lib';

import {TransactionServiceCdkStack} from './transaction_service_cdk-stack'
import { LinuxBuildImage } from "aws-cdk-lib/aws-codebuild";

export interface PipelineStackProps extends StackProps {
    readonly betaStoryServiceStack: TransactionServiceCdkStack;
    readonly betaAccountId: string;
    readonly betaAccessKeyId: string;
    readonly betaSecretKey: string;
} 
