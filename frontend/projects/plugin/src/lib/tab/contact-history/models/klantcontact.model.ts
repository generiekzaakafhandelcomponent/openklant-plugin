export interface Klantcontact {
  id?: string;
  number?: string;
  referenceId?: string;
  channel: string;
  subject: string;
  content?: string;
  reaction?: string;
  outcome: ContactOutcome;
  preferredLanguage: string;
  isConfidential: boolean;
  occurredAt?: Date;
}

export enum ContactOutcome {
  SUCCESS = "success",
  FAILURE = "failure",
  NOT_APPLICABLE = "notApplicable",
  UNKNOWN = "unknown",
}

export interface KlantcontactDTO {
  nummer?: string;
  referentienummer?: string;
  kanaal: string;
  onderwerp: string;
  inhoud?: string;
  reactie?: string;
  // The plugin used to serialise booleans as strings ('true'), so documents written before that was
  // fixed still hold the string form. Both encodings are accepted.
  indicatieContactGelukt?: boolean | string | null;
  taal: string;
  vertrouwelijk: boolean | string;
  plaatsgevondenOp?: string;
}

export function mapDtoToModel(dto: KlantcontactDTO): Klantcontact {
  return {
    id: dto.referentienummer
      ? dto.referentienummer
      : dto.nummer,
    number: dto.nummer,
    referenceId: dto.referentienummer,
    channel: dto.kanaal,
    subject: dto.onderwerp,
    content: dto.inhoud,
    reaction: dto.reactie,
    outcome: parseWasSuccessfulToContactOutcome(dto.indicatieContactGelukt),
    preferredLanguage: dto.taal,
    isConfidential: dto.vertrouwelijk === true || dto.vertrouwelijk === "true",
    occurredAt: dto.plaatsgevondenOp
      ? new Date(dto.plaatsgevondenOp)
      : undefined,
  };
}

export function mapModelToDto(model: Klantcontact): KlantcontactDTO {
  return {
    nummer: model.number,
    referentienummer: model.referenceId,
    kanaal: model.channel,
    onderwerp: model.subject,
    inhoud: model.content,
    reactie: model.reaction,
    indicatieContactGelukt: parseContactOutcomeToBoolean(model.outcome),
    taal: model.preferredLanguage,
    vertrouwelijk: model.isConfidential,
    plaatsgevondenOp: model.occurredAt
      ? model.occurredAt.toISOString()
      : undefined,
  };
}

function parseWasSuccessfulToContactOutcome(
  wasSuccessful?: boolean | string | null
): ContactOutcome {
  switch (wasSuccessful) {
    case null:
      return ContactOutcome.NOT_APPLICABLE;
    case true:
    case "true":
      return ContactOutcome.SUCCESS;
    case false:
    case "false":
      return ContactOutcome.FAILURE;
    default:
      return ContactOutcome.UNKNOWN;
  }
}

function parseContactOutcomeToBoolean(
  outcome: ContactOutcome
): string | null | undefined {
  switch (outcome) {
    case ContactOutcome.SUCCESS:
      return "true";
    case ContactOutcome.FAILURE:
      return "false";
    case ContactOutcome.NOT_APPLICABLE:
      return null;
    case ContactOutcome.UNKNOWN:
      return undefined;
  }
}
