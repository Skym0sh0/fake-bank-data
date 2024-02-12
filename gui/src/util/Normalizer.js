export function denormalizeUser(u) {
    if (!u)
        return null

    return {
        id: u.id,
        username: u.username,
        firstname: u.firstname,
        lastname: u.lastname,
    }
}

export function normalizeCategory(cat) {
    if (!cat)
        return null

    return {
        id: cat.id || null,
        parentId: cat.parentId || null,
        name: cat.name,
        description: cat.description,
    }
}

export function denormalizeCategory(cat) {
    return {
        id: cat.id,
        parentId: cat.parentId,
        name: cat.name,
        description: cat.description,
        isNew: cat.isNew,

        subCategories: (cat.subCategories || []).map(denormalizeCategory),

        createdAt: cat.createdAt,
        updatedAt: cat.updatedAt,

        usageCount: cat.usageCount,
    }
}

export function denormalizeTurnoverImport(turnoverImport) {
    return {
        id: turnoverImport.id,
        importedAt: turnoverImport.importedAt,

        filename: turnoverImport.filename,
        filesizeBytes: turnoverImport.filesizeBytes,
        encoding: turnoverImport.encoding,
        format: turnoverImport.format,

        firstTurnover: turnoverImport.firstTurnover,
        lastTurnover: turnoverImport.lastTurnover,

        turnovers: (turnoverImport.turnovers || []).map(denormalizeTurnoverRow),
    };
}

export function denormalizeTurnoverPreview(turnover) {
    return {
        filename: turnover.filename,
        format: turnover.format,
        uploadTime: turnover.uploadTime,
        rows: (turnover.rows || []).map(denormalizeTurnoverPreviewRow)
    };
}

export function denormalizeTurnoverRow(row) {
    return {
        id: row.id,
        date: row.date,
        amountInCents: row.amountInCents,
        categoryId: row.categoryId,
        checksum: row.checksum,
        description: row.description,
        suggestedCategory: row.suggestedCategory,
        recipient: row.recipient,
    }
}

export function normalizeTurnoversFromPreview(turnovers) {
    if (!turnovers)
        return []

    return turnovers.map(normalizeTurnoverFromPreview)
}

function normalizeTurnoverFromPreview(row) {
    return {
        date: row.date,
        amountInCents: row.amountInCents,
        categoryId: row.categoryId,
        suggestedCategory: row.suggestedCategory,
        description: row.description,
        recipient: row.recipient,
        checksum: row.checksum,
        similarityChecksum: row.similarityChecksum,
    }
}

export function denormalizeTurnoverPreviewRow(row) {
    return {
        date: row.date,
        amountInCents: row.amountInCents,
        categoryId: row.categoryId,
        suggestedCategories: (row.suggestedCategories || []).map(denormalizeTurnoverPreviewCategorySuggestion),
        suggestedCategory: row.suggestedCategory,
        description: row.description,
        recipient: row.recipient,
        checksum: row.checksum,
        similarityChecksum: row.similarityChecksum,
        importable: row.importable,
    };
}

export function denormalizeTurnoverPreviewCategorySuggestion(suggestions) {
    return {
        categoryId: suggestions.categoryId,
        frequency: suggestions.frequency || 0,
        precedence: suggestions.precedence || 0.0,
        origins: suggestions.origins || [],
    };
}

export function denormalizeBasicReportInfo(info) {
    return {
        userId: info.userId,
        earliestTransaction: info.earliestTransaction,
        latestTransaction: info.latestTransaction,
        numberOfTransactions: info.numberOfTransactions,
        numberOfUsedCategories: info.numberOfUsedCategories,
        maxDepthOfCategories: info.maxDepthOfCategories,
    }
}
