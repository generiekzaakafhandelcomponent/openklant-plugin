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
} from '@valtimo/plugin';
import {
  Observable,
  BehaviorSubject,
  Subscription,
  combineLatest,
  take, ReplaySubject,
} from "rxjs";
import {StoreContactInfoConfig} from '../../models/store-contact-info-config';
import {FormModule, FormOutput, InputModule} from '@valtimo/components';
import {AsyncPipe, NgIf} from '@angular/common';

@Component({
  selector: 'store-contact-info',
  standalone: true,
  imports: [
    FormModule,
    NgIf,
    InputModule,
    PluginTranslatePipeModule,
    AsyncPipe
  ],
  templateUrl: './open-klant-store-contact-info.component.html'
})
export class StoreContactInfoComponent
  implements FunctionConfigurationComponent, OnInit, OnDestroy {
  @Input() save$: Observable<void>;
  @Input() disabled$: Observable<boolean>;
  @Input() pluginId: string;
  @Input() prefillConfiguration$: Observable<StoreContactInfoConfig>;

  @Output() valid = new EventEmitter<boolean>();
  @Output() configuration = new EventEmitter<FunctionConfigurationData>();

  private readonly config$ =
    new BehaviorSubject<StoreContactInfoConfig | null>(null);
  private readonly valid$ = new BehaviorSubject<boolean>(false);
  private saveSubscription: Subscription;

  ngOnInit(): void {
    this.openSaveSubscription();
  }

  ngOnDestroy(): void {
    this.saveSubscription?.unsubscribe();
  }

  formValueChange(formOutput: FormOutput): void {
    this.config$.next(formOutput as StoreContactInfoConfig);
    this.handleValid(formOutput as StoreContactInfoConfig);
  }

  private handleValid(formOutput: StoreContactInfoConfig): void {
    const valid =
      !!formOutput.bsn &&
      !!formOutput.firstName &&
      !!formOutput.inFix &&
      !!formOutput.lastName &&
      !!formOutput.emailAddress &&
      !!formOutput.caseUuid;

    this.valid$.next(valid);
    this.valid.emit(valid);
  }

  private openSaveSubscription(): void {
    this.saveSubscription = this.save$?.subscribe(() => {
      combineLatest([this.config$, this.valid$])
        .pipe(take(1))
        .subscribe(([config, valid]) => {
          if (valid) {
            this.configuration.emit(config);
          }
        });
    });
  }
}
