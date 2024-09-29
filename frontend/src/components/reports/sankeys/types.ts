import {DateTime} from "luxon";
import {ReportTimeUnits} from "@api/api.ts";

export type BasicInfo = {
    earliest: DateTime;
    latest: DateTime,
    maxDepth: number;
    numberOfTransactions: number;
    numberOfUsedCategories: number;
}

export const AssociationsType = {
    Absolute: "Absolut",
    Relative: "Relativ",
}
export type AssociationsType = typeof AssociationsType[keyof typeof AssociationsType];

export type SelectType = {
    mode: AssociationsType;
    depth: number;
    year?: number;
    month?: number;
    referenceDate: DateTime;
    timeunit: ReportTimeUnits;
    units: number;
}
