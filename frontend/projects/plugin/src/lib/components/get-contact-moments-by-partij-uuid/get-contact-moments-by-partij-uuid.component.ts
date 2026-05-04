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
import {AsyncPipe, NgIf} from "@angular/common";
import {FormModule, FormOutput, InputModule, TooltipModule} from "@valtimo/components";
import {GetContactMomentsByPartijUuidConfig} from "../../models/get-contact-moments-by-partij-uuid-config";

@Component({
  selector: "get-contact-moments-by-partij-uuid",
  standalone: true,
  imports: [
    AsyncPipe,
    FormModule,
    InputModule,
    NgIf,
    PluginTranslatePipeModule,
    TooltipModule,
  ],
  templateUrl: "./get-contact-moments-by-partij-uuid.component.html"
})
export class GetContactMomentsByPartijUuidComponent
  implements FunctionConfigurationComponent, OnInit, OnDestroy {
  @Input() save$: Observable<void>;
  @Input() disabled$: Observable<boolean>;
  @Input() pluginId: string;
  @Input() prefillConfiguration$?: Observable<GetContactMomentsByPartijUuidConfig>;
  @Output() valid: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() configuration: EventEmitter<FunctionConfigurationData> =
    new EventEmitter<FunctionConfigurationData>();

  private saveSubscription!: Subscription;

  private readonly formValue$ =
    new ReplaySubject<GetContactMomentsByPartijUuidConfig>(1);
  private readonly valid$ = new BehaviorSubject<boolean>(false);

  ngOnInit(): void {
    this.openSaveSubscription();
  }

  ngOnDestroy(): void {
    this.saveSubscription?.unsubscribe();
  }

  formValueChange(formValue: FormOutput): void {
    this.formValue$.next(formValue as GetContactMomentsByPartijUuidConfig);
    this.handleValid(formValue as GetContactMomentsByPartijUuidConfig);
  }

  private handleValid(formValue: GetContactMomentsByPartijUuidConfig): void {
    const valid = !!formValue.resultPvName && !!formValue.partijUuid;

    this.valid$.next(valid);
    this.valid.emit(valid);
  }

  private openSaveSubscription(): void {
    this.saveSubscription = this.save$?.subscribe((save) => {
      combineLatest([this.formValue$, this.valid$])
        .pipe(take(1))
        .subscribe(([formValue, valid]) => {
          if (valid && formValue) {
            this.configuration.emit(formValue);
          }
        });
    });
  }
}
