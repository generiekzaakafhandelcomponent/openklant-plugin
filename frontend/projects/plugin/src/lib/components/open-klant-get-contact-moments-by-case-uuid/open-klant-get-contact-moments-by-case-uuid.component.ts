import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from "@angular/core";
import {
  FunctionConfigurationComponent,
  FunctionConfigurationData,
  PluginTranslatePipeModule,
} from "@valtimo/plugin";
import {
  BehaviorSubject,
  combineLatest,
  Observable, ReplaySubject,
  Subscription,
  take,
} from "rxjs";
import { AsyncPipe, NgIf } from "@angular/common";
import {FormModule, FormOutput, InputModule} from "@valtimo/components";
import { GetContactMomentsByCaseUuidConfig } from "../../models/get-contact-moments-by-case-uuid-config";

@Component({
  selector: "get-contact-moments-by-case-uuid",
  standalone: true,
  imports: [
    AsyncPipe,
    FormModule,
    InputModule,
    NgIf,
    PluginTranslatePipeModule,
  ],
  templateUrl: "./open-klant-get-contact-moments-by-case-uuid.component.html",
  styleUrl: "./open-klant-get-contact-moments-by-case-uuid.component.scss",
})
export class GetContactMomentsByCaseUuidComponent
  implements FunctionConfigurationComponent, OnInit, OnDestroy {
  @Input() save$: Observable<void>;
  @Input() disabled$: Observable<boolean>;
  @Input() pluginId: string;
  @Input()
  prefillConfiguration$?: Observable<GetContactMomentsByCaseUuidConfig>;
  @Output() valid: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() configuration: EventEmitter<FunctionConfigurationData> =
    new EventEmitter<FunctionConfigurationData>();

  private saveSubscription!: Subscription;

  private readonly formValue$ =
    new ReplaySubject<GetContactMomentsByCaseUuidConfig>(1);
  private readonly valid$ = new BehaviorSubject<boolean>(false);

  ngOnInit(): void {
    this.openSaveSubscription();
  }

  ngOnDestroy(): void {
    this.saveSubscription?.unsubscribe();
  }

  formValueChange(formValue: FormOutput): void {
    this.formValue$.next(formValue as GetContactMomentsByCaseUuidConfig);
    this.handleValid(formValue as GetContactMomentsByCaseUuidConfig);
  }

  private handleValid(formValue: GetContactMomentsByCaseUuidConfig): void {
    const valid = !!formValue.resultPvName && !!formValue.caseUuid;

    this.valid$.next(valid);
    this.valid.emit(valid);
  }

  private openSaveSubscription(): void {
    this.saveSubscription = this.save$?.subscribe((save) => {
      combineLatest([this.formValue$, this.valid$])
        .pipe(take(1))
        .subscribe(([formValue, valid]) => {
          if (valid) this.configuration.emit(formValue);
        });
    });
  }
}
